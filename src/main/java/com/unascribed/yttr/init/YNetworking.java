package com.unascribed.yttr.init;

import java.util.Locale;
import java.util.UUID;

import com.unascribed.yttr.util.YLog;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.util.Attackable;
import com.unascribed.yttr.util.math.Vec2i;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;

import com.google.common.collect.Multiset;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class YNetworking {

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "attack"), (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				if (player != null && player.getMainHandStack().getItem() instanceof Attackable) {
					((Attackable)player.getMainHandStack().getItem()).attack(player);
				}
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_pos"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player instanceof DiverPlayer) {
				DiverPlayer diver = (DiverPlayer)player;
				
				int x = buf.readInt();
				int z = buf.readInt();
				server.execute(() -> {
					if (diver.yttr$isDiving()) {
						int ticks = server.getTicks();
						int lastUpdate = diver.yttr$getLastDivePosUpdate();
						int diff = ticks-lastUpdate;
						diver.yttr$setLastDivePosUpdate(ticks);
						if (lastUpdate != 0 && diff < 4) {
							YLog.warn("{} is updating their dive pos too quickly!", player.getName().getString());
							correctDivePos(diver, responseSender);
							return;
						}
						Vec2i vec = new Vec2i(x, z);
						int distSq = vec.squaredDistanceTo(diver.yttr$getDivePos());
						if (distSq == 0) return;
						int moveSpeed = Yttr.DIVING_BLOCKS_PER_TICK;
						ItemStack is = player.getEquippedStack(EquipmentSlot.CHEST);
						if (!(is.getItem() instanceof SuitArmorItem)) return;
						SuitArmorItem sai = (SuitArmorItem)is.getItem();
						for (SuitResource sr : SuitResource.VALUES) {
							moveSpeed /= sr.getSpeedDivider(sai.getResourceAmount(is, sr) <= 0);
						}
						int max = (moveSpeed+1)*diff;
						if (distSq > max*max) {
							YLog.warn("{} dove too quickly! {}, {}", player.getName().getString(), x-diver.yttr$getDivePos().x, z-diver.yttr$getDivePos().z);
							correctDivePos(diver, responseSender);
							return;
						}
						double dist = MathHelper.sqrt(distSq);
						int pressure = Yttr.calculatePressure(player.getServerWorld(), diver.yttr$getDivePos().x, diver.yttr$getDivePos().z);
						for (SuitResource sr : SuitResource.VALUES) {
							sai.consumeResource(is, sr, sr.getConsumptionPerBlock(pressure)*(int)dist);
						}
						YStats.add(player, YStats.BLOCKS_DOVE, (int)(dist*100));
						diver.yttr$setDivePos(vec);
						PacketByteBuf resp = new PacketByteBuf(Unpooled.buffer(8));
						resp.writeVarInt(pressure);
						responseSender.sendPacket(new Identifier("yttr", "dive_pressure"), resp);
					}
				});
			}
		});
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_to"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player instanceof DiverPlayer) {
				DiverPlayer diver = (DiverPlayer)player;
				
				UUID id = buf.readUuid();
				server.execute(() -> {
					if (diver.yttr$isDiving() && diver.yttr$getFastDiveTarget() == null && diver.yttr$getKnownGeysers().contains(id)) {
						Geyser g = GeysersState.get(player.getServerWorld()).getGeyser(id);
						if (g != null) {
							double distance = Math.sqrt(g.pos.getSquaredDistance(diver.yttr$getDivePos().x, g.pos.getY(), diver.yttr$getDivePos().z, true));
							Multiset<SuitResource> resourcesNeeded = Yttr.determineNeededResourcesForFastDive(distance);
							Multiset<SuitResource> resourcesAvailable = Yttr.determineAvailableResources(player);
							if (!player.isCreative()) {
								for (SuitResource sr : SuitResource.VALUES) {
									if (resourcesAvailable.count(sr) < resourcesNeeded.count(sr)) {
										informCantDive(responseSender, "not enough "+sr.name().toLowerCase(Locale.ROOT));
										return;
									}
								}
							}
							ItemStack is = player.getEquippedStack(EquipmentSlot.CHEST);
							SuitArmorItem sai = (SuitArmorItem)is.getItem();
							for (SuitResource sr : SuitResource.VALUES) {
								sai.consumeResource(is, sr, resourcesNeeded.count(sr));
							}
							int time = (int)((distance/Yttr.DIVING_BLOCKS_PER_TICK)/5);
							diver.yttr$setFastDiveTarget(g.pos);
							diver.yttr$setFastDiveTime(time);
							YStats.add(player, YStats.BLOCKS_DOVE, (int)(distance*100));
							PacketByteBuf res = PacketByteBufs.create();
							for (SuitResource sr : SuitResource.VALUES) {
								res.writeVarInt(resourcesNeeded.count(sr));
							}
							res.writeVarInt(g.pos.getX());
							res.writeVarInt(g.pos.getZ());
							res.writeVarInt(time);
							responseSender.sendPacket(new Identifier("yttr", "animate_fastdive"), res);
						} else {
							informCantDive(responseSender, "unknown geyser");
							return;
						}
					} else {
						informCantDive(responseSender, "bad state");
						return;
					}
				});
			}
		});
	}
	
	private static void informCantDive(PacketSender responseSender, String msg) {
		PacketByteBuf res = PacketByteBufs.create();
		res.writeString(msg);
		responseSender.sendPacket(new Identifier("yttr", "cant_dive"), res);
	}

	private static void correctDivePos(DiverPlayer diver, PacketSender responseSender) {
		PacketByteBuf resp = new PacketByteBuf(Unpooled.buffer(8));
		resp.writeInt(diver.yttr$getDivePos().x);
		resp.writeInt(diver.yttr$getDivePos().z);
		responseSender.sendPacket(new Identifier("yttr", "dive_pos"), resp);
	}
	
}

package com.unascribed.yttr.network;

import java.util.Locale;
import java.util.UUID;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.network.concrete.C2SMessage;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;

import com.google.common.collect.Multiset;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageC2SDiveTo extends C2SMessage {

	public UUID id;
	
	public MessageC2SDiveTo(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SDiveTo(UUID id) {
		super(YNetwork.CONTEXT);
		this.id = id;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (player instanceof DiverPlayer) {
			DiverPlayer diver = (DiverPlayer)player;
			if (diver.yttr$isDiving() && diver.yttr$getFastDiveTarget() == null && diver.yttr$getKnownGeysers().contains(id)) {
				Geyser g = GeysersState.get(player.getServerWorld()).getGeyser(id);
				if (g != null) {
					double distance = Math.sqrt(g.pos.getSquaredDistance(diver.yttr$getDivePos().x, g.pos.getY(), diver.yttr$getDivePos().z, true));
					Multiset<SuitResource> resourcesNeeded = Yttr.determineNeededResourcesForFastDive(distance);
					Multiset<SuitResource> resourcesAvailable = Yttr.determineAvailableResources(player);
					if (!player.isCreative()) {
						for (SuitResource sr : SuitResource.VALUES) {
							if (resourcesAvailable.count(sr) < resourcesNeeded.count(sr)) {
								new MessageS2CDiveError("not enough "+sr.name().toLowerCase(Locale.ROOT)).sendTo(player);
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
					new MessageS2CAnimateFastDive(resourcesNeeded, g.pos.getX(), g.pos.getZ(), time).sendTo(player);
				} else {
					new MessageS2CDiveError("unknown geyser").sendTo(player);
					return;
				}
			} else {
				new MessageS2CDiveError("bad state").sendTo(player);
				return;
			}
		}
	}

}

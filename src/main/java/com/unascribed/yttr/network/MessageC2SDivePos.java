package com.unascribed.yttr.network;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.network.concrete.C2SMessage;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.util.math.Vec2i;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class MessageC2SDivePos extends C2SMessage {

	@MarshalledAs("varint")
	public int x;
	@MarshalledAs("varint")
	public int z;
	
	public MessageC2SDivePos(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SDivePos(int x, int z) {
		super(YNetwork.CONTEXT);
		this.x = x;
		this.z = z;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (!(player instanceof DiverPlayer)) return;
		DiverPlayer diver = (DiverPlayer)player;
		if (diver.yttr$isDiving()) {
			int ticks = player.getServer().getTicks();
			int lastUpdate = diver.yttr$getLastDivePosUpdate();
			int diff = ticks-lastUpdate;
			diver.yttr$setLastDivePosUpdate(ticks);
			if (lastUpdate != 0 && diff < 4) {
				YLog.warn("{} is updating their dive pos too quickly!", player.getName().getString());
				new MessageS2CDivePos(diver.yttr$getDivePos().x, diver.yttr$getDivePos().z).sendTo(player);
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
				new MessageS2CDivePos(diver.yttr$getDivePos().x, diver.yttr$getDivePos().z).sendTo(player);
				return;
			}
			double dist = MathHelper.sqrt(distSq);
			int pressure = Yttr.calculatePressure(player.getServerWorld(), diver.yttr$getDivePos().x, diver.yttr$getDivePos().z);
			for (SuitResource sr : SuitResource.VALUES) {
				sai.consumeResource(is, sr, sr.getConsumptionPerBlock(pressure)*(int)dist);
			}
			YStats.add(player, YStats.BLOCKS_DOVE, (int)(dist*100));
			diver.yttr$setDivePos(vec);
			new MessageS2CDivePressure(pressure).sendTo(player);
		}
	}

}

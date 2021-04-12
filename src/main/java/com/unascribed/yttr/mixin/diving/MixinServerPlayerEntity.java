package com.unascribed.yttr.mixin.diving;

import java.util.Set;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.DiverPlayer;
import com.unascribed.yttr.EquipmentSlots;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.item.SuitArmorItem;

import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements DiverPlayer {

	private boolean yttr$isDiving = false;
	private boolean yttr$isInvisibleFromDiving = false;
	private boolean yttr$isNoGravityFromDiving = false;
	
	private final Set<UUID> yttr$knownGeysers = Sets.newHashSet();
	
	@Inject(at=@At("HEAD"), method="tick")
	public void tick(CallbackInfo ci) {
		ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
		if (!self.isAlive()) return;
		if (yttr$isDiving) {
			if (self.getPos().y > 0) {
				yttr$isDiving = false;
			} else {
				if (!self.isInvisible()) {
					self.setInvisible(true);
					yttr$isInvisibleFromDiving = true;
				} else if (!self.hasNoGravity()) {
					self.setNoGravity(true);
					yttr$isNoGravityFromDiving = true;
				}
				self.setPos(self.getPos().x, -12, self.getPos().z);
			}
			boolean fail = true;
			for (EquipmentSlot slot : EquipmentSlots.ARMOR) {
				ItemStack is = self.getEquippedStack(slot);
				if (is.getItem() instanceof SuitArmorItem) {
					SuitArmorItem sai = ((SuitArmorItem)is.getItem());
					if (sai.getIntegrityDamage(is) < sai.getIntegrity(is)) {
						fail = false;
					}
				} else {
					fail = true;
					break;
				}
			}
			if (fail) {
				self.damage(new DamageSource("yttr.suit_integrity_failure") {{
					setUnblockable();
					setBypassesArmor();
					setOutOfWorld();
				}}, self.getHealth()*6);
			} else {
				while (true) {
					EquipmentSlot slot = EquipmentSlots.ARMOR.get(self.world.random.nextInt(EquipmentSlots.ARMOR.size()));
					ItemStack piece = self.getEquippedStack(slot);
					SuitArmorItem sai = ((SuitArmorItem)piece.getItem());
					if (sai.getIntegrityDamage(piece) < sai.getIntegrity(piece)) {
						sai.damageIntegrity(piece, 1);
						break;
					}
				}
			}
		} else {
			if (yttr$isNoGravityFromDiving) {
				self.setNoGravity(false);
			}
			if (yttr$isInvisibleFromDiving) {
				self.setInvisible(false);
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="onSpawn")
	public void onSpawn(CallbackInfo ci) {
		if (yttr$isDiving) {
			ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
			Yttr.syncDive(self);
		}
	}
	
	@Inject(at=@At("TAIL"), method="writeCustomDataToTag")
	public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		if (yttr$isDiving) tag.putBoolean("yttr:Diving", yttr$isDiving);
		if (yttr$isInvisibleFromDiving) tag.putBoolean("yttr:InvisibleFromDiving", yttr$isInvisibleFromDiving);
		if (yttr$isNoGravityFromDiving) tag.putBoolean("yttr:NoGravityFromDiving", yttr$isNoGravityFromDiving);
		
		if (!yttr$knownGeysers.isEmpty()) {
			ListTag li = new ListTag();
			for (UUID id : yttr$knownGeysers) {
				li.add(NbtHelper.fromUuid(id));
			}
			tag.put("yttr:KnownGeysers", li);
		}
	}
	
	@Inject(at=@At("TAIL"), method="readCustomDataFromTag")
	public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		System.out.println("read");
		yttr$isDiving = tag.getBoolean("yttr:Diving");
		yttr$isInvisibleFromDiving = tag.getBoolean("yttr:InvisibleFromDiving");
		yttr$isNoGravityFromDiving = tag.getBoolean("yttr:NoGravityFromDiving");
		
		yttr$knownGeysers.clear();
		ListTag li = tag.getList("yttr:KnownGeysers", NbtType.INT_ARRAY);
		for (int i = 0; i < li.size(); i++) {
			yttr$knownGeysers.add(NbtHelper.toUuid(li.get(i)));
		}
	}
	
	@Inject(at=@At("TAIL"), method="copyFrom")
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		if (oldPlayer instanceof DiverPlayer) {
			yttr$knownGeysers.addAll(((DiverPlayer)oldPlayer).yttr$getKnownGeysers());
		}
	}
	
	@Override
	public boolean yttr$isDiving() {
		return yttr$isDiving;
	}

	@Override
	public void yttr$setDiving(boolean b) {
		yttr$isDiving = b;
	}
	
	@Override
	public boolean yttr$isInvisibleFromDiving() {
		return yttr$isInvisibleFromDiving;
	}
	
	@Override
	public boolean yttr$isNoGravityFromDiving() {
		return yttr$isNoGravityFromDiving;
	}
	
	@Override
	public Set<UUID> yttr$getKnownGeysers() {
		return yttr$knownGeysers;
	}

}

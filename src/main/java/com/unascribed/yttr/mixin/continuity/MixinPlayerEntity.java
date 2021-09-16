package com.unascribed.yttr.mixin.continuity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@ModifyVariable(at=@At("HEAD"), method="dropSelectedItem", argsOnly=true, ordinal=0)
	public boolean modifyDropEntireStack(boolean orig) {
		if (getStackInHand(Hand.MAIN_HAND).getItem() == YItems.DISC_OF_CONTINUITY) {
			return false;
		}
		return orig;
	}

	@Inject(at=@At("RETURN"), method="dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
	public void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> ci) {
		if (retainOwnership && stack.getItem() == YItems.DISC_OF_CONTINUITY && pitch >= 70) {
			ItemEntity ie = ci.getReturnValue();
			((Entity)ie).age = 4;
			ie.calculateDimensions();
			ie.setNoGravity(true);
			ie.setVelocity(ie.getVelocity().x, 0, ie.getVelocity().z);
			ie.setPosition(getPos().x, getPos().y-0.3, getPos().z);
		}
	}
	
}

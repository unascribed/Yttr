package com.unascribed.yttr.mixin.replicator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

	@ModifyVariable(at=@At("HEAD"), method="spawnItemParticles", argsOnly=true, ordinal=0)
	public ItemStack modifyStack(ItemStack stack) {
		if (stack.getItem() == YItems.REPLICATOR) {
			return ReplicatorBlockItem.getHeldItem(stack);
		}
		return stack;
	}
	
}

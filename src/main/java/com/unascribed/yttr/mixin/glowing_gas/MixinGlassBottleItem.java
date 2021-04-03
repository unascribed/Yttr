package com.unascribed.yttr.mixin.glowing_gas;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(GlassBottleItem.class)
public abstract class MixinGlassBottleItem {

	@Inject(at=@At("HEAD"), method="use", cancellable=true)
	public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
		List<AreaEffectCloudEntity> clouds = world.getEntitiesByClass(AreaEffectCloudEntity.class, user.getBoundingBox().expand(2), (cloud) ->
			cloud != null && cloud.isAlive() && cloud.getName().asString().equals("§e§6§eGlowdampCloud") && cloud.getRadius() > 0);
		ItemStack stack = user.getStackInHand(hand);
		if (!clouds.isEmpty()) {
			AreaEffectCloudEntity cloud = clouds.get(0);
			cloud.setRadius(cloud.getRadius() - 0.3f);
			if (cloud.getRadius() <= 0) cloud.remove();
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1, 1);
			ci.setReturnValue(TypedActionResult.success(fill(stack, user, new ItemStack(YItems.GLOWING_GAS)), world.isClient()));
		}
	}

	@Shadow
	protected abstract ItemStack fill(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2);

}

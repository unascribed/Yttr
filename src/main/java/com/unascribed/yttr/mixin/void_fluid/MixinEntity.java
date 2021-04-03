package com.unascribed.yttr.mixin.void_fluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YTags;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

@Mixin(Entity.class)
public class MixinEntity {

	@Shadow
	protected boolean firstUpdate;
	@Shadow
	protected Object2DoubleMap<Tag<Fluid>> fluidHeight;
	
	@Inject(at=@At("HEAD"), method="isInsideWaterOrBubbleColumn()Z", cancellable=true)
	public void isInsideWaterOrBubbleColumn(CallbackInfoReturnable<Boolean> ci) {
		if (!firstUpdate && fluidHeight.getDouble(YTags.Fluid.VOID_TAG) > 0) {
			ci.setReturnValue(true);
		}
	}
	
}

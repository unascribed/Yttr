package com.unascribed.yttr.mixin.cleaver.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.SlopeStander;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

@Mixin(Camera.class)
public abstract class MixinCamera {

	@Shadow
	protected abstract void setPos(Vec3d pos);
	@Shadow
	public abstract Vec3d getPos();
	
	@Inject(at=@At("TAIL"), method="update")
	public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
		double yO = MathHelper.lerp(tickDelta, ((SlopeStander)focusedEntity).yttr$getLastYOffset(), ((SlopeStander)focusedEntity).yttr$getYOffset());
		if (yO != 0) {
			setPos(getPos().add(0, yO, 0));
		}
	}
	
}

package com.unascribed.yttr.mixin.neodymium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.block.NeodymiumBlock.MagneticVoxelShape;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;

@Mixin(Entity.class)
public class MixinEntity {

	private boolean yttr$magnetizedBelow;
	private boolean yttr$magnetizedAbove;
	private boolean yttr$magnetizedAboveStuck;
	
	@Inject(at=@At("TAIL"), method="baseTick")
	protected void baseTick(CallbackInfo ci) {
		yttr$magnetizedBelow = false;
		yttr$magnetizedAbove = false;
		yttr$magnetizedAboveStuck = false;
		Entity self = (Entity)(Object)this;
		if (self instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)self;
			if (le.getEquippedStack(EquipmentSlot.FEET).getItem().isIn(YTags.Item.MAGNETIC)) {
				Box box = self.getBoundingBox();
				Box bottom = new Box(box.minX, box.minY-0.5, box.minZ, box.maxX, box.minY, box.maxZ);
				if (self.world.getBlockCollisions(self, bottom).anyMatch(vs -> vs instanceof MagneticVoxelShape)) {
					yttr$magnetizedBelow = true;
				}
			}
			if (le.getEquippedStack(EquipmentSlot.HEAD).getItem().isIn(YTags.Item.MAGNETIC)) {
				Box box = self.getBoundingBox();
				Box top = new Box(box.minX, box.maxY, box.minZ, box.maxX, box.maxY+0.5, box.maxZ);
				if (self.world.getBlockCollisions(self, top).anyMatch(vs -> {
					if (!(vs instanceof MagneticVoxelShape)) return false;
					double min = vs.getMin(Axis.Y);
					if (min < box.maxY) return false;
					if (min == box.maxY) yttr$magnetizedAboveStuck = true;
					return true;
				})) {
					yttr$magnetizedAbove = true;
				}
			}
		}
		if (yttr$magnetizedAbove) {
			self.setVelocity(self.getVelocity().x, yttr$magnetizedAboveStuck ? 0 : Math.max(self.getVelocity().y, 0.05), self.getVelocity().z);
		} else if (yttr$magnetizedBelow) {
			self.setVelocity(self.getVelocity().x, Math.min(self.getVelocity().y, -0.9), self.getVelocity().z);
		}
	}
	
	@Inject(at=@At("RETURN"), method="getJumpVelocityMultiplier", cancellable=true)
	protected void getJumpVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		if (yttr$magnetizedBelow) ci.setReturnValue(ci.getReturnValueF()*0.1f);
		if (yttr$magnetizedAbove) ci.setReturnValue(0f);
	}

	@Inject(at=@At("RETURN"), method="getVelocityMultiplier", cancellable=true)
	protected void getVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		if (yttr$magnetizedBelow) ci.setReturnValue(ci.getReturnValueF()*0.2f);
		if (yttr$magnetizedAbove) ci.setReturnValue(ci.getReturnValueF()*0.1f);
	}
	
	@Inject(at=@At("HEAD"), method="playStepSound")
	protected void playStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
		if (yttr$magnetizedBelow) {
			Entity self = (Entity)(Object)this;
			self.playSound(YSounds.MAGNET_STEP, 1, 1);
		}
	}
	
}

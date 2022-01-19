package com.unascribed.yttr.mixin.neodymium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.block.NeodymiumBlock.MagneticVoxelShape;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixinsupport.Magnetized;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;

@Mixin(Entity.class)
public class MixinEntity implements Magnetized {

	private static final DamageSource YTTR$MAGNET = new DamageSource("yttr.magnet") {{
		setBypassesArmor();
		setUnblockable();
		setScaledWithDifficulty();
	}};
	
	private boolean yttr$magnetizedBelow;
	private boolean yttr$magnetizedAbove;
	private boolean yttr$magnetizedAboveStuck;
	
	@Inject(at=@At("TAIL"), method="baseTick")
	protected void baseTick(CallbackInfo ci) {
		yttr$magnetTick();
	}

	@Override
	public void yttr$magnetTick() {
		yttr$magnetizedBelow = false;
		yttr$magnetizedAbove = false;
		yttr$magnetizedAboveStuck = false;
		boolean receptiveAbove = false;
		boolean receptiveBelow = false;
		Entity self = (Entity)(Object)this;
		if (self.getType() == null || self.world == null || self.getBoundingBox() == null) return;
		if (self instanceof AbstractMinecartEntity || self.getType().isIn(YTags.Entity.MAGNETIC)) {
			receptiveBelow = true;
			receptiveAbove = true;
		} else if (self instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)self;
			if (le.getEquippedStack(EquipmentSlot.FEET).getItem().isIn(YTags.Item.MAGNETIC)) {
				receptiveBelow = true;
			}
			if (le.getEquippedStack(EquipmentSlot.HEAD).getItem().isIn(YTags.Item.MAGNETIC)) {
				receptiveAbove = true;
			}
		} else if (self instanceof ItemEntity) {
			if (((ItemEntity)self).getStack().getItem().isIn(YTags.Item.MAGNETIC)) {
				receptiveBelow = true;
				receptiveAbove = true;
			}
		}
		if (receptiveBelow) {
			Box box = self.getBoundingBox();
			Box bottom = new Box(box.minX, box.minY-0.5, box.minZ, box.maxX, box.minY, box.maxZ);
			if (self.world.getBlockCollisions(self, bottom).anyMatch(vs -> vs instanceof MagneticVoxelShape)) {
				yttr$magnetizedBelow = true;
			}
		}
		if (receptiveAbove) {
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
		if (yttr$magnetizedAbove) {
			self.setVelocity(self.getVelocity().x, Math.max(self.getVelocity().y, 0.1), self.getVelocity().z);
			if (yttr$magnetizedBelow && !(self instanceof IronGolemEntity) && !(self instanceof ItemEntity)) {
				self.damage(YTTR$MAGNET, 2);
			}
			if (Math.abs(self.pitch) > 0.01 && self.world.random.nextInt(20) == 0) {
				self.playSound(YSounds.MAGNET_STEP, 1, 1);
			}
			self.pitch /= 2;
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

	@Override
	public boolean yttr$isMagnetizedBelow() {
		return yttr$magnetizedBelow;
	}

	@Override
	public boolean yttr$isMagnetizedAbove() {
		return yttr$magnetizedAbove;
	}

	@Override
	public boolean yttr$isMagnetizedAboveStuck() {
		return yttr$magnetizedAboveStuck;
	}
	
}

package com.unascribed.yttr.mixin.continuity;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Shadow
	public abstract ItemStack getStack();

	@Inject(at=@At("TAIL"), method="tick")
	public void tick(CallbackInfo ci) {
		if (getStack().getItem() == YItems.DISC_OF_CONTINUITY) {
			if (!yttr$disc) {
				yttr$disc = true;
				calculateDimensions();
				updatePosition(getPos().x, getPos().y, getPos().z);
			}
			ItemEntity ie = (ItemEntity)(Object)this;
			if (ie.getThrower() != null) {
				PlayerEntity thrower = world.getPlayerByUuid(ie.getThrower());
				if (thrower != null && thrower.squaredDistanceTo(this) > 16*16) {
					yttr$forcingPickup = true;
					try {
						((ItemEntity)(Object)this).onPlayerCollision(thrower);
					} finally {
						yttr$forcingPickup = false;
					}
				}
			}
			if (age == 5) {
				setNoGravity(true);
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="setStack")
	public void setStack(ItemStack stack, CallbackInfo ci) {
		yttr$disc = false;
		if (stack != null) {
			if (stack.getItem() == YItems.DROP_OF_CONTINUITY) {
				setNoGravity(true);
			} else if (stack.getItem() == YItems.DISC_OF_CONTINUITY) {
				yttr$disc = true;
				calculateDimensions();
			}
		}
	}
	
	// can't inject into a method defined in a superclass, have to @Override it and conflict :(
	// ...or do you?
	// (thanks to Emi for this solution)
	
	// of course, this doesn't do any good if others don't use this trick too, but modding knowledge
	// is spread via source code more and more lately. maybe someone will check to see how Yttr does
	// this and see this :)
	
	@Intrinsic
	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		return super.interact(player, hand);
	}

	@Intrinsic
	@Override
	public boolean isCollidable() { // hasHardCollision
		return super.isCollidable();
	}

	@Intrinsic
	@Override
	public boolean collides() { // occludesRaycasts
		return super.collides();
	}
	
	@Intrinsic
	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return super.getDimensions(pose);
	}
	
	@Intrinsic
	@Override
	public void move(MovementType type, Vec3d movement) {
		super.move(type, movement);
	}
	
	private boolean yttr$disc;
	private boolean yttr$forcingPickup;
	
	@Inject(at=@At("HEAD"), method="canMerge", cancellable=true)
	private void canMerge(CallbackInfoReturnable<Boolean> ci) {
		if (yttr$disc) {
			ci.setReturnValue(false);
		}
	}
	
	@Inject(at=@At("HEAD"), method="onPlayerCollision", cancellable=true)
	public void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
		if (yttr$disc && !yttr$forcingPickup) {
			ci.cancel();
		}
	}
	
	@Inject(at=@At("HEAD"), method="interact", cancellable=true)
	public void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
		if (yttr$disc) {
			ItemStack stack = player.getStackInHand(hand);
			if (stack.isEmpty() || stack.getItem() == YItems.DISC_OF_CONTINUITY) {
				yttr$forcingPickup = true;
				try {
					((ItemEntity)(Object)this).onPlayerCollision(player);
				} finally {
					yttr$forcingPickup = false;
				}
				ci.setReturnValue(ActionResult.SUCCESS);
			} else {
				if (player.world.isClient) {
					ci.setReturnValue(ActionResult.CONSUME);
				} else {
					ci.setReturnValue(stack.useOnBlock(new ItemUsageContext(player, hand, new BlockHitResult(getPos().add(0, 1, 0), Direction.UP, getBlockPos().up(), false))));
				}
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="isCollidable", cancellable=true)
	public void isCollidable(CallbackInfoReturnable<Boolean> ci) {
		if (yttr$disc && hasNoGravity()) {
			ci.setReturnValue(true);
		}
	}
	
	@Inject(at=@At("HEAD"), method="collides", cancellable=true)
	public void collides(CallbackInfoReturnable<Boolean> ci) {
		if (yttr$disc) {
			ci.setReturnValue(true);
		}
	}
	
	@Inject(at=@At("HEAD"), method="getDimensions", cancellable=true)
	public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> ci) {
		if (yttr$disc) {
			ci.setReturnValue(EntityDimensions.fixed(0.8f, 0.2f));
		}
	}
	
	@Inject(at=@At("HEAD"), method="move", cancellable=true)
	public void moveHead(MovementType type, Vec3d movement, CallbackInfo ci) {
		if (yttr$disc) {
			boolean substantial = movement.lengthSquared() > 0.05*0.05;
			for (Entity e : world.getOtherEntities(this, getBoundingBox().expand(0, 0.005, 0))) {
				if ((substantial || e.isSneaking()) && e.getPos().y > getPos().y) {
					e.move(MovementType.SHULKER, movement);
				}
			}
		}
	}
	
}

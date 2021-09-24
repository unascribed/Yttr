package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceBlockEntity {

	@Shadow
	private int burnTime;
	
	@Inject(at=@At("TAIL"), method="writeNbt")
	public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> ci) {
		if (burnTime > 32767) {
			nbt.putInt("BurnTime", burnTime);
		}
	}
	
	@Inject(at=@At("TAIL"), method="readNbt")
	public void readNbt(BlockState state, NbtCompound tag, CallbackInfo ci) {
		if (tag.getInt("BurnTime") != this.burnTime) {
			burnTime = tag.getInt("BurnTime");
		}
	}
	
}

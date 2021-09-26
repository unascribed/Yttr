package com.unascribed.yttr.mixin.soaking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.WetWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(at=@At("TAIL"), method="tick")
	public void tick(CallbackInfo ci) {
		if (world instanceof WetWorld) {
			BlockPos pos = getBlockPos();
			if (!world.getFluidState(pos).isEmpty()) {
				((WetWorld)world).yttr$getSoakingMap().put(pos, (ItemEntity)(Object)this);
			}
		}
	}
	
}

package com.unascribed.yttr.mixin.continuity;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.EndIslandFeature;

@Mixin(EndIslandFeature.class)
public class MixinEndIslandFeature {

	@Inject(at=@At("TAIL"), method="generate")
	public void generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig, CallbackInfoReturnable<Boolean> ci) {
		if (ci.getReturnValueZ()) {
			BlockPos.Mutable cur = blockPos.mutableCopy();
			BlockState bs = structureWorldAccess.getBlockState(cur);
			int i = 0;
			while (!bs.isAir()) {
				if (i++ > 50) return;
				cur.move(Direction.DOWN);
				bs = structureWorldAccess.getBlockState(cur);
			}
			cur.move(Direction.UP);
			structureWorldAccess.setBlockState(cur, YBlocks.ROOT_OF_CONTINUITY.getDefaultState(), 3);
		}
	}
	
}

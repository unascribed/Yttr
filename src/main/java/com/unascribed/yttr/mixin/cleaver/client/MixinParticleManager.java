package com.unascribed.yttr.mixin.cleaver.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

	@Shadow
	protected ClientWorld world;
	
	@ModifyVariable(at=@At(value="NEW", target="net/minecraft/client/particle/BlockDustParticle"),
			method="addBlockBreakingParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V", ordinal=0)
	public BlockState modBlockState(BlockState in, BlockPos pos) {
		if (in.isOf(YBlocks.CLEAVED_BLOCK)) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof CleavedBlockEntity) {
				return ((CleavedBlockEntity) be).getDonor();
			}
		}
		return in;
	}
	
}

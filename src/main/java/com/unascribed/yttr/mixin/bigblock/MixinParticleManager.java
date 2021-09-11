package com.unascribed.yttr.mixin.bigblock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.BigBlock;

import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {

	@Shadow
	protected ClientWorld world;
	
	@Shadow
	public abstract void addBlockBreakingParticles(BlockPos pos, Direction dir);
	
	private boolean yttr$reentering = false;
	
	@Inject(at=@At("HEAD"), method="addBlockBreakingParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V",
			cancellable=true)
	public void addBlockBreakingParticles(BlockPos pos, Direction dir, CallbackInfo ci) {
		if (yttr$reentering) return;
		BlockState bs = world.getBlockState(pos);
		if (bs.getBlock() instanceof BigBlock) {
			BigBlock b = (BigBlock)bs.getBlock();
			int bX = bs.get(b.X);
			int bY = bs.get(b.Y);
			int bZ = bs.get(b.Z);
			yttr$reentering = true;
			try {
				// there's probably a better way to do this, but whatever
				if (dir.getAxis() == Axis.X) {
					for (int y = -bY; y < b.ySize-bY; y++) {
						for (int z = -bZ; z < b.zSize-bZ; z++) {
							if (y == 0 && z == 0) continue;
							addBlockBreakingParticles(pos.add(0, y, z), dir);
						}
					}
				} else if (dir.getAxis() == Axis.Z) {
					for (int y = -bY; y < b.ySize-bY; y++) {
						for (int x = -bX; x < b.xSize-bX; x++) {
							if (y == 0 && x == 0) continue;
							addBlockBreakingParticles(pos.add(x, y, 0), dir);
						}
					}
				} else if (dir.getAxis() == Axis.Y) {
					for (int x = -bX; x < b.xSize-bX; x++) {
						for (int z = -bZ; z < b.zSize-bZ; z++) {
							if (x == 0 && z == 0) continue;
							addBlockBreakingParticles(pos.add(x, 0, z), dir);
						}
					}
				}
			} finally {
				yttr$reentering = false;
			}
		}
	}
	
}

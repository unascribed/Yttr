package com.unascribed.yttr.mixin.bigblock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.big.BigBlock;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Mixin(BlockRenderManager.class)
public abstract class MixinBlockRenderManager {

	@Shadow
	public abstract void renderDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer);
	
	private boolean yttr$reentering;
	
	@Inject(at=@At("HEAD"), method="renderDamage(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V",
			cancellable=true)
	public void renderDamage(BlockState bs, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer, CallbackInfo ci) {
		if (yttr$reentering) return;
		if (bs.getBlock() instanceof BigBlock) {
			BigBlock b = (BigBlock)bs.getBlock();
			int bX = bs.get(b.xProp);
			int bY = bs.get(b.yProp);
			int bZ = bs.get(b.zProp);
			BlockPos origin = pos.add(-bX, -bY, -bZ);
			yttr$reentering = true;
			try {
				for (int x = 0; x < b.xSize; x++) {
					for (int y = 0; y < b.ySize; y++) {
						for (int z = 0; z < b.zSize; z++) {
							BlockPos bp = origin.add(x, y, z);
							matrix.push();
								matrix.translate(bp.getX()-pos.getX(), bp.getY()-pos.getY(), bp.getZ()-pos.getZ());
								renderDamage(world.getBlockState(bp), bp, world, matrix, vertexConsumer);
							matrix.pop();
						}
					}
				}
			} finally {
				yttr$reentering = false;
			}
			ci.cancel();
		}
	}
	
}

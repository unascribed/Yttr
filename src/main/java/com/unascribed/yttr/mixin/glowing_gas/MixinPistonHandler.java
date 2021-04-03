package com.unascribed.yttr.mixin.glowing_gas;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(PistonHandler.class)
public class MixinPistonHandler {

	@Shadow @Final
	private World world;
	@Shadow @Final
	private List<BlockPos> brokenBlocks;
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/block/BlockState.getPistonBehavior"), method="tryMove", cancellable=true,
			locals=LocalCapture.CAPTURE_FAILHARD)
	public void tryMove(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> ci, BlockState state, Block b, int i, int j, int k, BlockPos moving) {
		if (world.getBlockState(moving).isOf(Blocks.SHROOMLIGHT)
				&& world.getBlockState(moving.offset(dir)).isOf(YBlocks.YTTRIUM_BLOCK)
				&& world.getBlockState(moving.offset(dir.getOpposite())).isOf(YBlocks.YTTRIUM_BLOCK)) {
			if (world.breakBlock(moving, false)) {
				double ofs = 0.5;
				if (world.getBlockState(moving.up()).isAir()) {
					ofs = 1;
				} else if (world.getBlockState(moving.down()).isAir()) {
					ofs = 0;
				}
				AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, moving.getX()+0.5, moving.getY()+ofs, moving.getZ()+0.5);
				cloud.setColor(0xFEAC6D);
				cloud.setRadius(1f);
				cloud.setDuration(200);
				cloud.setCustomName(new LiteralText("§e§6§eGlowdampCloud"));
				cloud.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5*20));
				cloud.addEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10*20));
				world.spawnEntity(cloud);
				ci.setReturnValue(true);
			}
		}
	}
	
}

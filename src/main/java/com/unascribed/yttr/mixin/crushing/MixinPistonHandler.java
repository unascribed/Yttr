package com.unascribed.yttr.mixin.crushing;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
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
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/block/BlockState.getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"), method="tryMove", cancellable=true,
			locals=LocalCapture.CAPTURE_FAILHARD)
	public void tryMove(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> ci, BlockState state, Block b, int i, int j, int k, BlockPos moving) {
		if (world.getBlockState(moving.offset(dir)).isOf(YBlocks.YTTRIUM_BLOCK)
				&& world.getBlockState(moving.offset(dir.getOpposite())).isOf(YBlocks.YTTRIUM_BLOCK)) {
			BlockState movingState = world.getBlockState(moving);
			if (movingState.isOf(Blocks.SHROOMLIGHT) || movingState.isOf(YBlocks.ULTRAPURE_CARBON_BLOCK)) {
				if (world.breakBlock(moving, false)) {
					double ofs = 0.5;
					if (world.getBlockState(moving.up()).isAir()) {
						ofs = 1;
					} else if (world.getBlockState(moving.down()).isAir()) {
						ofs = 0;
					}
					if (movingState.isOf(Blocks.SHROOMLIGHT)) {
						AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, moving.getX()+0.5, moving.getY()+ofs, moving.getZ()+0.5);
						cloud.setColor(0xFEAC6D);
						cloud.setRadius(1f);
						cloud.setDuration(100);
						cloud.setCustomName(new LiteralText("§e§6§eGlowdampCloud"));
						cloud.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5*20));
						cloud.addEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10*20));
						world.spawnEntity(cloud);
					} else {
						ItemStack stack = new ItemStack(YItems.COMPRESSED_ULTRAPURE_CARBON);
						ItemEntity item = new ItemEntity(world, moving.getX()+0.5, moving.getY()+ofs, moving.getZ()+0.5, stack);
						item.setVelocity(world.random.nextGaussian()/8, (ofs-0.5)/6, world.random.nextGaussian()/8);
						world.spawnEntity(item);
					}
					ci.setReturnValue(true);
				}
			}
		} else if (world.getBlockState(moving.offset(dir)).isOf(Blocks.DIAMOND_BLOCK)
				&& world.getBlockState(moving.offset(dir.getOpposite())).isOf(Blocks.DIAMOND_BLOCK)) {
			BlockState movingState = world.getBlockState(moving);
			if (movingState.isOf(YBlocks.ULTRAPURE_CARBON_BLOCK) || movingState.isOf(YBlocks.COMPRESSED_ULTRAPURE_CARBON_BLOCK)) {
				if (world.breakBlock(moving, false)) {
					double ofs = 0.5;
					if (world.getBlockState(moving.up()).isAir()) {
						ofs = 1;
					} else if (world.getBlockState(moving.down()).isAir()) {
						ofs = 0;
					}
					ItemStack stack;
					if (movingState.isOf(YBlocks.ULTRAPURE_CARBON_BLOCK)) {
						stack = new ItemStack(YItems.COMPRESSED_ULTRAPURE_CARBON);
					} else {
						stack = new ItemStack(YItems.ULTRAPURE_DIAMOND);
					}
					ItemEntity item = new ItemEntity(world, moving.getX()+0.5, moving.getY()+ofs, moving.getZ()+0.5, stack);
					item.setVelocity(world.random.nextGaussian()/8, (ofs-0.5)/6, world.random.nextGaussian()/8);
					world.spawnEntity(item);
					ci.setReturnValue(true);
				}
			}
		}
	}
	
}

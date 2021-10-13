package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.content.fluid.VoidFluid;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YSounds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class VoidCauldronBlock extends Block implements FluidDrainable, BlockEntityProvider {

	public VoidCauldronBlock(Settings settings) {
		super(settings);
	}

	@Override
	public String getTranslationKey() {
		return Blocks.CAULDRON.getTranslationKey();
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new VoidCauldronBlockEntity();
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.getY() > pos.getY()+0.125) {
			YBlocks.VOID.onEntityCollision(state, world, pos, entity);
		}
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() == Items.BUCKET) {
			return ActionResult.PASS;
		}
		if (!stack.isEmpty()) {
			player.setStackInHand(hand, ItemStack.EMPTY);
			player.playSound(YSounds.DISSOLVE, 1, 0.8f+(world.random.nextFloat()*0.4f));
			if (world instanceof ServerWorld) {
				((ServerWorld)world).spawnParticles(VoidFluid.BLACK_DUST, pos.getX()+0.5, pos.getY()+0.9, pos.getZ()+0.5, 30, 0.15, 0.1, 0.15, 0.5);
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Blocks.CAULDRON);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return Blocks.CAULDRON.getDefaultState().getOutlineShape(world, pos, context);
	}
	
	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return Blocks.CAULDRON.getDefaultState().getRaycastShape(world, pos);
	}

	@Override
	public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.CAULDRON.getDefaultState(), 3);
		return YFluids.VOID;
	}
	
	
}

package com.unascribed.yttr.content.block.big;

import com.unascribed.yttr.init.YSounds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DSUBlock extends BigBlock {
	
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final IntProperty X = IntProperty.of("x", 0, 1);
	public static final IntProperty Y = IntProperty.of("y", 0, 1);
	public static final IntProperty Z = IntProperty.of("z", 0, 1);
	
	public DSUBlock(Settings s) {
		super(X, Y, Z, s);
		setDefaultState(getDefaultState().with(OPEN, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(OPEN, FACING, X, Y, Z);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing().getOpposite());
	}
	
	@Override
	protected BlockState copyState(BlockState us, BlockState neighbor) {
		return us.with(OPEN, neighbor.get(OPEN));
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		world.setBlockState(pos, state.cycle(OPEN));
		if (state.get(OPEN)) {
			playSound(world, player, pos, state, YSounds.DSU_CLOSE, SoundCategory.BLOCKS, 1, 1);
		} else {
			playSound(world, player, pos, state, YSounds.DSU_OPEN, SoundCategory.BLOCKS, 1, 1);
		}
		return ActionResult.SUCCESS;
	}
	
}

package com.unascribed.yttr.content.block.ruined;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class RCStyleMultiblock extends Block {

	public static final BooleanProperty INSIDE = BooleanProperty.of("inside");
	
	private final int radius;
	private final int yplus;
	private final int yminus;
	
	public RCStyleMultiblock(int radius, int yplus, int yminus, Settings settings) {
		super(settings);
		this.radius = radius;
		this.yplus = yplus;
		this.yminus = yminus;
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(INSIDE);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return determineState(world, pos);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return determineState(ctx.getWorld(), ctx.getBlockPos());
	}
	
	private BlockState determineState(WorldAccess world, BlockPos pos) {
		boolean surrounded = false;
		for (Direction d : Direction.Type.HORIZONTAL) {
			if (world.getBlockState(pos.offset(d)).isAir()) {
				boolean surroundedThisPlane = true;
				glass: for (Direction d2 : Direction.values()) {
					if (d2.getAxis() == d.getAxis()) continue;
					int r = d2 == Direction.DOWN ? yminus : d2 == Direction.UP ? yplus : radius;
					for (int i = 0; i < r; i++) {
						BlockState bs = world.getBlockState(pos.offset(d2, i+1));
						if (!bs.isOf(this) || bs.get(INSIDE)) {
							surroundedThisPlane = false;
							break glass;
						}
					}
				}
				if (surroundedThisPlane) {
					surrounded = true;
					break;
				}
			}
		}
		return getDefaultState().with(INSIDE, surrounded);
	}

}

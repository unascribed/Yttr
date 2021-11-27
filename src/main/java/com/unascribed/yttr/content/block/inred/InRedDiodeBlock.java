package com.unascribed.yttr.content.block.inred;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InRedDiodeBlock extends InRedLogicTileBlock {
	public static final BooleanProperty BIT_0 = BooleanProperty.of("bit_0");
	public static final BooleanProperty BIT_1 = BooleanProperty.of("bit_1");
	public static final BooleanProperty BIT_2 = BooleanProperty.of("bit_2");
	public static final BooleanProperty BIT_3 = BooleanProperty.of("bit_3");
	public static final BooleanProperty BIT_4 = BooleanProperty.of("bit_4");
	public static final BooleanProperty BIT_5 = BooleanProperty.of("bit_5");

	public static final VoxelShape CLICK_BIT_0 = Block.createCuboidShape(10, 2.9, 10, 11, 4.1, 14);
	public static final VoxelShape CLICK_BIT_1 = Block.createCuboidShape(9, 2.9, 8, 10, 4.1, 12);
	public static final VoxelShape CLICK_BIT_2 = Block.createCuboidShape(8, 2.9, 10, 9, 4.1, 14);
	public static final VoxelShape CLICK_BIT_3 = Block.createCuboidShape(7, 2.9, 8, 8, 4.1, 12);
	public static final VoxelShape CLICK_BIT_4 = Block.createCuboidShape(6, 2.9, 10, 7, 4.1, 14);
	public static final VoxelShape CLICK_BIT_5= Block.createCuboidShape(5, 2.9, 8, 6, 4.1, 12);

	public InRedDiodeBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState()
				.with(BIT_0, true)
				.with(BIT_1, true)
				.with(BIT_2, true)
				.with(BIT_3, true)
				.with(BIT_4, true)
				.with(BIT_5, true)
				.with(FACING, Direction.NORTH)
				.with(WATERLOGGED, false));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new InRedDiodeBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof InRedDiodeBlockEntity) {
			Vec3d blockCenteredHit = hit.getPos();
			blockCenteredHit = blockCenteredHit.subtract(0.5, 0.5, 0.5);
			switch (state.get(FACING)) {
				case SOUTH:
					blockCenteredHit = blockCenteredHit.rotateY((float)Math.PI);
					break;
				case EAST:
					blockCenteredHit = blockCenteredHit.rotateY((float)Math.PI/2);
					break;
				case WEST:
					blockCenteredHit = blockCenteredHit.rotateY(3*(float)Math.PI/2);
					break;
				default:
					break;
			}
			blockCenteredHit = blockCenteredHit.add(0.5, 0.5, 0.5);
			InRedDiodeBlockEntity beDiode = (InRedDiodeBlockEntity) be;
			if (CLICK_BIT_0.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(0);
			}
			if (CLICK_BIT_1.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(1);
			}
			if (CLICK_BIT_2.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(2);
			}
			if (CLICK_BIT_3.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(3);
			}
			if (CLICK_BIT_4.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(4);
			}
			if (CLICK_BIT_5.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(5);
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(BIT_0, BIT_1, BIT_2, BIT_3, BIT_4, BIT_5);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDiodeBlockEntity) {
			return ((InRedDiodeBlockEntity)be).isActive()?16:0;
		}
		return 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof InRedDiodeBlockEntity) {
				world.setBlockState(pos, state
						.with(BIT_0, bitToBool(0, world, pos))
						.with(BIT_1, bitToBool(1, world, pos))
						.with(BIT_2, bitToBool(2, world, pos))
						.with(BIT_3, bitToBool(3, world, pos))
						.with(BIT_4, bitToBool(4, world, pos))
						.with(BIT_5, bitToBool(5, world, pos)));
			}
		}
	}

	public boolean bitToBool(int bit, BlockView world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof InRedDiodeBlockEntity) {
			InRedDiodeBlockEntity beDiode = (InRedDiodeBlockEntity) be;
			return (1<<bit & beDiode.getMask()) > 0;
		}
		return false;
	}
}

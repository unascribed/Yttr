package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.inred.InactiveSelection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InRedAndGateBlock extends InRedLogicTileBlock {
	public static final EnumProperty<InactiveSelection> INACTIVE = EnumProperty.of("inactive", InactiveSelection.class);

	private static final VoxelShape CLICK_LEFT = Block.createCuboidShape( 0, 2.9,  6,  3, 4.1, 10);
	private static final VoxelShape CLICK_BACK = Block.createCuboidShape( 6, 2.9, 13, 10, 4.1, 16);
	private static final VoxelShape CLICK_RIGHT = Block.createCuboidShape(13, 2.9,  6, 16, 4.1, 10);

	public InRedAndGateBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false).with(INACTIVE, InactiveSelection.NONE));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(BOOLEAN_MODE, INACTIVE);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new InRedAndGateBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof InRedAndGateBlockEntity) {
			Vec3d blockCenteredHit = new Vec3d(hit.getPos().getX() - hit.getBlockPos().getX(), hit.getPos().getY() - hit.getBlockPos().getY(), hit.getPos().getZ() - hit.getBlockPos().getZ());
			blockCenteredHit = blockCenteredHit.subtract(0.5, 0.5, 0.5);
			switch (state.get(InRedAndGateBlock.FACING)) {
				case SOUTH:
					blockCenteredHit = blockCenteredHit.rotateY((float)Math.PI);
					break;
				case EAST:
					blockCenteredHit = blockCenteredHit.rotateY((float)Math.PI / 2);
					break;
				case WEST:
					blockCenteredHit = blockCenteredHit.rotateY(3 * (float)Math.PI / 2);
					break;
				default:
					break;
			}
			blockCenteredHit = blockCenteredHit.add(0.5, 0.5, 0.5);
			InRedAndGateBlockEntity beAndGate = (InRedAndGateBlockEntity)be;
			if (CLICK_BOOLEAN.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleBooleanMode();
			}
			if (CLICK_LEFT.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleInactive(InactiveSelection.LEFT);
			}
			if (CLICK_BACK.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleInactive(InactiveSelection.BACK);
			}
			if (CLICK_RIGHT.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleInactive(InactiveSelection.RIGHT);
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedAndGateBlockEntity) {
			return ((InRedAndGateBlockEntity) be).isActive()? 16 : 0;
		}
		return 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			if (state.get(WATERLOGGED)) {
				world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof InRedAndGateBlockEntity) {
				world.setBlockState(pos, state
						.with(BOOLEAN_MODE, ((InRedAndGateBlockEntity)be).booleanMode)
						.with(INACTIVE, ((InRedAndGateBlockEntity)be).inactive));
			}
		}
	}
}

package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inventory.VoidFilterScreenHandler;
import com.unascribed.yttr.world.FilterNetworks;
import com.unascribed.yttr.world.FilterNetwork.NodeType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class VoidFilterBlock extends Block implements BlockEntityProvider {
	
	public static final BooleanProperty INDEPENDENT = BooleanProperty.of("independent");

	public VoidFilterBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(INDEPENDENT, true));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(INDEPENDENT);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new VoidFilterBlockEntity();
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.getBlockState(pos.down()).isOf(YBlocks.VOID_GEYSER) && !world.getBlockState(pos.down()).isOf(YBlocks.DORMANT_VOID_GEYSER)) return ActionResult.PASS;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof VoidFilterBlockEntity) {
			player.openHandledScreen(new NamedScreenHandlerFactory() {
				
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new VoidFilterScreenHandler((VoidFilterBlockEntity)be, syncId, inv, ((VoidFilterBlockEntity)be).getProperties());
				}
				
				@Override
				public Text getDisplayName() {
					return new TranslatableText("block.yttr.void_filter");
				}
			});
		}
		return ActionResult.SUCCESS;
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (world instanceof ServerWorld) {
			boolean valid = world.getBlockState(pos.down()).isOf(YBlocks.VOID_GEYSER) || world.getBlockState(pos.down()).isOf(YBlocks.DORMANT_VOID_GEYSER);
			FilterNetworks.get((ServerWorld)world).introduce(pos, valid ? NodeType.FILTER : NodeType.DEAD_FILTER);
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof VoidFilterBlockEntity) {
				ItemScatterer.spawn(world, pos, (VoidFilterBlockEntity)be);
			}
			if (world instanceof ServerWorld) {
				FilterNetworks.get((ServerWorld)world).destroy(pos);
			}
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		if (!state.isOf(oldState.getBlock())) {
			if (world instanceof ServerWorld) {
				boolean valid = world.getBlockState(pos.down()).isOf(YBlocks.VOID_GEYSER) || world.getBlockState(pos.down()).isOf(YBlocks.DORMANT_VOID_GEYSER);
				FilterNetworks.get((ServerWorld)world).introduce(pos, valid ? NodeType.FILTER : NodeType.DEAD_FILTER);
			}
		}
	}

}

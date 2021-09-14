package com.unascribed.yttr.content.block.device;

import java.util.Random;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inventory.VoidFilterScreenHandler;

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
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VoidFilterBlock extends Block implements BlockEntityProvider {
	
	public static final BooleanProperty ENABLED = Properties.ENABLED;

	public VoidFilterBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(ENABLED, true));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(ENABLED);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean cur = state.get(ENABLED);
			if (cur == world.isReceivingRedstonePower(pos)) {
				if (cur) {
					world.getBlockTickScheduler().schedule(pos, this, 4);
				} else {
					world.setBlockState(pos, state.with(ENABLED, false), 2);
				}
			}

		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isReceivingRedstonePower(pos)) {
			world.setBlockState(pos, state.with(ENABLED, true), 2);
		}
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
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof VoidFilterBlockEntity) {
				ItemScatterer.spawn(world, pos, (VoidFilterBlockEntity)be);
			}
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

}

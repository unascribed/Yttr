package com.unascribed.yttr.content.block.big;

import com.unascribed.yttr.inventory.MagtankScreenHandler;
import com.unascribed.yttr.world.FilterNetworks;
import com.unascribed.yttr.world.FilterNetwork.NodeType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagtankBlock extends BigBlock {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final IntProperty X = IntProperty.of("x", 0, 1);
	public static final IntProperty Y = IntProperty.of("y", 0, 2);
	public static final IntProperty Z = IntProperty.of("z", 0, 1);
	
	public MagtankBlock(Settings s) {
		super(X, Y, Z, s);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING, X, Y, Z);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing().getOpposite());
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		if (!state.isOf(oldState.getBlock())) {
			if (world instanceof ServerWorld) {
				FilterNetworks.get((ServerWorld)world).introduce(pos, NodeType.TANK);
			}
		}
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		if (!newState.isOf(state.getBlock())) {
			if (world instanceof ServerWorld) {
				FilterNetworks.get((ServerWorld)world).destroy(pos);
			}
		}
	}
	
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		player.openHandledScreen(new NamedScreenHandlerFactory() {
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
				return new MagtankScreenHandler(syncId, inv);
			}
			
			@Override
			public Text getDisplayName() {
				return new TranslatableText("block.yttr.magtank");
			}
		});
		return ActionResult.SUCCESS;
	}
	
}

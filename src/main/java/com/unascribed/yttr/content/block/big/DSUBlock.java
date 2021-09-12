package com.unascribed.yttr.content.block.big;

import java.util.Random;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.inventory.HighStackGenericContainerScreenHandler;

import com.google.common.base.Ascii;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DSUBlock extends BigBlock implements BlockEntityProvider {
	
	public enum OpenState implements StringIdentifiable {
		FALSE,
		TRUE,
		FORCED,
		;
		
		public boolean isFalse() {
			return this == FALSE;
		}
		
		public boolean isTrue() {
			return this != FALSE;
		}
		
		public boolean isForced() {
			return this == FORCED;
		}

		@Override
		public String asString() {
			return Ascii.toLowerCase(name());
		}
	}
	
	public static final EnumProperty<OpenState> OPEN = EnumProperty.of("open", OpenState.class);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final IntProperty X = IntProperty.of("x", 0, 1);
	public static final IntProperty Y = IntProperty.of("y", 0, 1);
	public static final IntProperty Z = IntProperty.of("z", 0, 1);
	
	public DSUBlock(Settings s) {
		super(X, Y, Z, s);
		setDefaultState(getDefaultState().with(OPEN, OpenState.FALSE));
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
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean cur = state.get(OPEN).isForced();
			if (cur != isReceivingRedstonePower(world, pos, state)) {
				if (cur) {
					world.getBlockTickScheduler().schedule(pos, this, 4);
				} else {
					if (!state.get(OPEN).isTrue() && !anyNeighborsMatch(world, pos, state, bs -> bs.get(OPEN).isForced())) {
						playSound(world, null, pos, state, YSounds.DSU_OPEN, SoundCategory.BLOCKS, 1, 1);
					}
					world.setBlockState(pos, state.with(OPEN, OpenState.FORCED));
				}
			}

		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		if (!isReceivingRedstonePower(world, pos, state)) {
			BlockEntity be = world.getBlockEntity(pos);
			boolean open = false;
			if (be instanceof DSUBlockEntity) {
				DSUBlockEntity con = ((DSUBlockEntity)be).getController();
				open = con.viewers > 0;
			}
			if (!open && state.get(OPEN).isTrue()) {
				playSound(world, null, pos, state, YSounds.DSU_CLOSE, SoundCategory.BLOCKS, 1, 1);
			}
			world.setBlockState(pos, state.with(OPEN, open ? OpenState.TRUE : OpenState.FALSE), 2);
		}
	}
	
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (hit.getSide() == state.get(FACING)) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof DSUBlockEntity) {
				player.openHandledScreen(new NamedScreenHandlerFactory() {
					
					@Override
					public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
						return HighStackGenericContainerScreenHandler.createGeneric9x5(syncId, inv, (DSUBlockEntity)be);
					}
					
					@Override
					public Text getDisplayName() {
						return new TranslatableText("block.yttr.dsu");
					}
				});
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new DSUBlockEntity();
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock()) && state.get(X) == 0 && state.get(Y) == 0 && state.get(Z) == 0) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof DSUBlockEntity) {
				double x = pos.getX()+(xSize/2D);
				double y = pos.getY()+(ySize/2D);
				double z = pos.getZ()+(zSize/2D);
				for (ItemStack is : Yttr.asList((DSUBlockEntity)be)) {
					double xO = (world.random.nextDouble()-world.random.nextDouble())*0.5;
					double yO = (world.random.nextDouble()-world.random.nextDouble())*0.5;
					double zO = (world.random.nextDouble()-world.random.nextDouble())*0.5;
					ItemScatterer.spawn(world, x+xO, y+yO, z+zO, is);
				}
			}
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
}

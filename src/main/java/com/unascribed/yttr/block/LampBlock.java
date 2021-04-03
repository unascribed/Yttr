package com.unascribed.yttr.block;

import java.util.List;
import java.util.Random;

import com.unascribed.yttr.LampColor;
import com.unascribed.yttr.block.entity.LampBlockEntity;
import com.unascribed.yttr.item.block.LampBlockItem;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LampBlock extends Block implements BlockEntityProvider {

	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	public static final EnumProperty<LampColor> COLOR = EnumProperty.of("color", LampColor.class);
	
	public LampBlock(Settings settings) {
		super(settings
				.emissiveLighting((state, view, pos) -> state.get(LIT))
				.luminance((state) -> state.get(LIT) ? 15 : 0));
		setDefaultState(getDefaultState().with(INVERTED, false).with(LIT, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, INVERTED, COLOR);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new LampBlockEntity();
	}
	
	private ItemStack getDrop(BlockState state) {
		ItemStack is = new ItemStack(this);
		LampBlockItem.setInverted(is, state.get(INVERTED));
		LampBlockItem.setColor(is, state.get(COLOR));
		return is;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
		return Lists.newArrayList(getDrop(state));
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getDrop(state);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		boolean powered = ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos());
		boolean inverted = LampBlockItem.isInverted(ctx.getStack());
		LampColor color = LampBlockItem.getColor(ctx.getStack());
		return getDefaultState()
				.with(LIT, powered^inverted)
				.with(INVERTED, inverted)
				.with(COLOR, color);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean cur = state.get(LIT);
			if (cur != (world.isReceivingRedstonePower(pos) ^ state.get(INVERTED))) {
				if (cur) {
					world.getBlockTickScheduler().schedule(pos, this, 4);
				} else {
					world.setBlockState(pos, state.cycle(LIT), 2);
				}
			}

		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isReceivingRedstonePower(pos) ^ state.get(INVERTED)) {
			world.setBlockState(pos, state.cycle(LIT), 2);
		}
	}
	
	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
		int rem = 9-(LampColor.values().length%9);
		for (LampColor color : LampColor.values()) {
			list.add(getDrop(getDefaultState().with(COLOR, color)));
		}
		for (int i = 0; i < rem; i++) {
			list.add(ItemStack.EMPTY);
		}
		for (LampColor color : LampColor.values()) {
			list.add(getDrop(getDefaultState().with(COLOR, color).with(INVERTED, true)));
		}
		for (int i = 0; i < rem; i++) {
			list.add(ItemStack.EMPTY);
		}
	}

}

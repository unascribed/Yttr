package com.unascribed.yttr;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LampBlock extends Block implements BlockEntityProvider {

	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);
	
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
		is.setTag(new CompoundTag());
		is.getTag().putBoolean("Inverted", state.get(INVERTED));
		is.getTag().putByte("DyeColor", (byte)state.get(COLOR).getId());
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
		boolean inverted = ctx.getStack().hasTag() && ctx.getStack().getTag().getBoolean("Inverted");
		DyeColor color = ctx.getStack().hasTag() ? DyeColor.byId(ctx.getStack().getTag().getByte("DyeColor")&0xFF) : DyeColor.WHITE;
		return getDefaultState()
				.with(LIT, powered^inverted)
				.with(INVERTED, inverted)
				.with(COLOR, color);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean cur = state.get(LIT) ^ state.get(INVERTED);
			if (cur != world.isReceivingRedstonePower(pos)) {
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
		if (world.isReceivingRedstonePower(pos) ^ state.get(INVERTED)) {
			world.setBlockState(pos, state.cycle(LIT), 2);
		}
	}
	
	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
		for (DyeColor color : DyeColor.values()) {
			list.add(getDrop(getDefaultState().with(COLOR, color)));
			list.add(getDrop(getDefaultState().with(COLOR, color).with(INVERTED, true)));
		}
	}

}
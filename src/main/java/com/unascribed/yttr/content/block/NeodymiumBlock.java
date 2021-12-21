package com.unascribed.yttr.content.block;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mechanics.SimpleLootBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NeodymiumBlock extends SlabBlock implements SimpleLootBlock {

	public NeodymiumBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		if (ctx.getStack().getItem() == YItems.NEODYMIUM_BLOCK)
			return getDefaultState().with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		return super.getPlacementState(ctx);
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getLoot(state);
	}

	@Override
	public ItemStack getLoot(BlockState state) {
		return new ItemStack(state.get(TYPE) == SlabType.DOUBLE ? YItems.NEODYMIUM_BLOCK : YItems.NEODYMIUM_SLAB);
	}
	
	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
		list.add(new ItemStack(YItems.NEODYMIUM_BLOCK));
		list.add(new ItemStack(YItems.NEODYMIUM_SLAB));
	}
	
	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		if (entity instanceof PlayerEntity && Yttr.setSoleTrinket != null && Yttr.isWearingCoil((PlayerEntity)entity)) {
			PlayerEntity pe = (PlayerEntity)entity;
			entity.dropStack(Yttr.getSoleTrinket.apply(pe));
			Yttr.setSoleTrinket.accept(pe, ItemStack.EMPTY);
		}
	}
	
}

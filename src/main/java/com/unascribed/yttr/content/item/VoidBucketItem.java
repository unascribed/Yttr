package com.unascribed.yttr.content.item;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

public class VoidBucketItem extends BucketItem {

	public VoidBucketItem(Settings settings) {
		super(YFluids.VOID, settings);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		BlockState bs = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (bs.isOf(Blocks.CAULDRON)) {
			ctx.getWorld().setBlockState(ctx.getBlockPos(), YBlocks.VOID_CAULDRON.getDefaultState());
			ctx.getPlayer().setStackInHand(ctx.getHand(), new ItemStack(Items.BUCKET));
			ctx.getWorld().playSound(null, ctx.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1, 1);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

}

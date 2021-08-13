package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YSounds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ReplicatorBlock extends Block implements BlockEntityProvider {

	public ReplicatorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ReplicatorBlockEntity();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.CONSUME;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity) {
			ReplicatorBlockEntity rbe = (ReplicatorBlockEntity)be;
			ItemStack held = player.getStackInHand(hand);
			if (player.isSneaking() && held.isEmpty()) {
				player.setStackInHand(hand, rbe.getStack(0));
				world.playSound(null, pos, YSounds.REPLICATOR_VEND, SoundCategory.BLOCKS, 1, 1);
				return ActionResult.SUCCESS;
			} else if (player.isCreative() || player.getUuid().equals(rbe.owner)) {
				if (!ItemStack.areItemsEqual(held, rbe.item) || !ItemStack.areTagsEqual(held, rbe.item)) {
					rbe.item = held.copy();
					world.playSound(null, pos, YSounds.REPLICATOR_UPDATE, SoundCategory.BLOCKS, 1, rbe.item.isEmpty() ? 1f : 1.25f);
					rbe.sync();
					return ActionResult.SUCCESS;
				} else {
					return ActionResult.CONSUME;
				}
			} else {
				world.playSound(null, pos, YSounds.REPLICATOR_REFUSE, SoundCategory.BLOCKS, 1, 0.75f);
				world.playSound(null, pos, YSounds.REPLICATOR_REFUSE, SoundCategory.BLOCKS, 1, 0.6f);
				return ActionResult.CONSUME;
			}
		}
		return ActionResult.FAIL;
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		if (!player.isCreative()) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof ReplicatorBlockEntity) {
				ReplicatorBlockEntity rbe = (ReplicatorBlockEntity)be;
				if (player.getUuid().equals(rbe.owner)) {
					return 1;
				}
			}
		}
		return 0;
	}
	
	private ItemStack getStack(BlockView world, BlockPos pos) {
		ItemStack stack = new ItemStack(this);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity) {
			stack.putSubTag("BlockEntityTag", be.toTag(new CompoundTag()));
		}
		return stack;
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getStack(world, pos);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity && placer instanceof PlayerEntity && !((PlayerEntity)placer).isCreative()) {
			((ReplicatorBlockEntity)be).owner = placer.getUuid();
		}
		world.playSound(placer instanceof PlayerEntity ? ((PlayerEntity)placer) : null, pos, YSounds.REPLICATOR_APPEAR, SoundCategory.BLOCKS, 1, 1);
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		world.playSound(player, pos, YSounds.REPLICATOR_DISAPPEAR, SoundCategory.BLOCKS, 1, 1);
		if (!player.isCreative()) {
			dropStack(world, pos, getStack(world, pos));
		}
	}

}

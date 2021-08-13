package com.unascribed.yttr.content.block.abomination;

import java.util.List;

import com.unascribed.yttr.content.block.decor.TableBlock;
import com.unascribed.yttr.content.item.block.SkeletalSorterBlockItem;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;

import com.google.common.base.Ascii;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SkeletalSorterBlock extends TableBlock implements BlockEntityProvider {

	public enum StateableArm implements StringIdentifiable {
		LEFT(Arm.LEFT),
		RIGHT(Arm.RIGHT),
		;
		public final Arm arm;
		
		StateableArm(Arm arm) {
			this.arm = arm;
		}
		
		@Override
		public String asString() {
			return Ascii.toLowerCase(name());
		}
	}
	
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final EnumProperty<StateableArm> MAIN_HAND = EnumProperty.of("main_hand", StateableArm.class);
	public static final BooleanProperty ENGOGGLED = BooleanProperty.of("engoggled");
	public static final BooleanProperty MUTED = BooleanProperty.of("muted");
	
	public SkeletalSorterBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(ENGOGGLED, false).with(MUTED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING, MAIN_HAND, ENGOGGLED, MUTED);
	}
	
	private ItemStack getDrop(BlockState state) {
		return state.get(MAIN_HAND) == StateableArm.LEFT ? new ItemStack(YItems.SKELETAL_SORTER_LEFT_HANDED) : new ItemStack(YItems.SKELETAL_SORTER_RIGHT_HANDED);
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getDrop(state);
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		return Lists.newArrayList(getDrop(state));
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SkeletalSorterBlockEntity();
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be != null) {
			be.onSyncedBlockEvent(type, data);
		}
		return true;
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing());
		if (ctx.getStack().getItem() instanceof SkeletalSorterBlockItem) {
			state = state.with(MAIN_HAND, ((SkeletalSorterBlockItem)ctx.getStack().getItem()).mainHand == Arm.LEFT ? StateableArm.LEFT : StateableArm.RIGHT);
		}
		return state;
	}
	
	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, state, blockEntity, stack);
		world.playSound(null, pos, YSounds.SKELETAL_SORTER_HURT, SoundCategory.BLOCKS, 1, (world.random.nextFloat()-world.random.nextFloat())*0.2f + 1);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem().isIn(ItemTags.WOOL) && !state.get(MUTED)) {
			world.playSound(player, pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1, 1.2f);
			world.setBlockState(pos, state.with(MUTED, true));
			return ActionResult.SUCCESS;
		} else if (stack.getItem() == YItems.GOGGLES && !state.get(ENGOGGLED)) {
			if (!player.abilities.creativeMode) {
				stack.decrement(1);
				player.setStackInHand(hand, stack);
			}
			world.playSound(player, pos, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.BLOCKS, 1, 1);
			world.setBlockState(pos, state.with(ENGOGGLED, true));
			return ActionResult.SUCCESS;
		} else {
			if (state.get(ENGOGGLED) && !stack.getItem().isIn(ItemTags.WOOL)) {
				if (stack.isEmpty()) {
					player.setStackInHand(hand, new ItemStack(YItems.GOGGLES));
				} else {
					player.inventory.offerOrDrop(world, new ItemStack(YItems.GOGGLES));
				}
				world.playSound(player, pos, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.BLOCKS, 1, 1.2f);
				world.setBlockState(pos, state.with(ENGOGGLED, false));
				return ActionResult.SUCCESS;
			} else if (state.get(MUTED)) {
				world.playSound(player, pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1, 0.8f);
				world.setBlockState(pos, state.with(MUTED, false));
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}
	
}

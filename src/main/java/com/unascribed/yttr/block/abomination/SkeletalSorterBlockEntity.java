package com.unascribed.yttr.block.abomination;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.Iterables;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class SkeletalSorterBlockEntity extends AbstractAbominationBlockEntity implements BlockEntityClientSerializable {

	public static final int THINK_TIME = 80;
	public static final int STOW_TIME = 30;
	
	public ItemStack heldItemMainHand = ItemStack.EMPTY;
	public ItemStack heldItemOffHand = ItemStack.EMPTY;
	
	public int thinkTicks;
	public int stowTicks;
	
	public Hand stowing;
	
	public Direction accessingInventory;
	
	public SkeletalSorterBlockEntity() {
		super(YBlockEntities.SKELETAL_SORTER);
	}

	@Override
	public Vec3d getHeadPos() {
		return new Vec3d(pos.getX()+0.5, pos.getY()+1.25, pos.getZ()+0.5);
	}
	
	@Override
	public void tick() {
		super.tick();
		if (world.isClient) {
			if (thinkTicks > 0 && thinkTicks < THINK_TIME) {
				thinkTicks++;
			} else if (stowTicks > 0 && stowTicks < STOW_TIME) {
				stowTicks++;
			}
			return;
		}
		
		Direction facing = getCachedState().get(SkeletalSorterBlock.FACING);
		ItemFrameEntity itemFrame = getItemFrame();
		if (itemFrame == null) {
			cleanUpAccessingInventory();
			return;
		}
		Inventory input = obtainInventory(facing);
		if (input == null) {
			cleanUpAccessingInventory();
			return;
		}
		Inventory right = obtainInventory(facing.rotateYClockwise());
		if (right == null) {
			cleanUpAccessingInventory();
			return;
		}
		Inventory left = obtainInventory(facing.rotateYCounterclockwise());
		if (left == null) {
			cleanUpAccessingInventory();
			return;
		}
		
		Arm mainHand = getCachedState().get(SkeletalSorterBlock.MAIN_HAND).arm;
		
		Inventory mainHandOutput = mainHand == Arm.LEFT ? left : right;
		Direction mainHandDir = mainHand == Arm.LEFT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
		Inventory offHandOutput = mainHand == Arm.LEFT ? right : left;
		Direction offHandDir = mainHand == Arm.LEFT ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
		
		Direction newAccessingInventory = accessingInventory;
		
		if (stowTicks == 0 && heldItemMainHand.isEmpty()) {
			thinkTicks = 0;
			newAccessingInventory = facing;
			ItemStack stack = ItemStack.EMPTY;
			for (int i = 0; i < input.size(); i++) {
				ItemStack inSlot = input.getStack(i);
				if (inSlot.isEmpty()) continue;
				if (input instanceof SidedInventory) {
					if (!((SidedInventory)input).canExtract(i, inSlot, Direction.UP)) continue;
				}
				stack = input.removeStack(i, Math.min(inSlot.getCount(), 8));
				break;
			}
			if (!stack.isEmpty()) {
				heldItemMainHand = stack;
				newAccessingInventory = null;
				sync();
				world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 1, 0);
				thinkTicks = 1;
			}
		} else if (thinkTicks > 0) {
			if (thinkTicks < THINK_TIME) {
				if (thinkTicks < 20) {
					newAccessingInventory = facing;
				} else {
					newAccessingInventory = null;
				}
				thinkTicks++;
			} else {
				thinkTicks = 0;
				if (ItemStack.areItemsEqualIgnoreDamage(itemFrame.getHeldItemStack(), heldItemMainHand)) {
					stowing = Hand.MAIN_HAND;
					stowTicks = 1;
					world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 0, 1);
				} else {
					stowing = Hand.OFF_HAND;
					stowTicks = 1;
					if (!heldItemOffHand.isEmpty()) {
						Block.dropStack(world, pos.up(), heldItemOffHand);
					}
					heldItemOffHand = heldItemMainHand;
					heldItemMainHand = ItemStack.EMPTY;
					world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 0, 2);
					sync();
				}
			}
		} else if (stowTicks > 0) {
			if (stowing == null) {
				stowTicks = 0;
			} else if (stowTicks < STOW_TIME) {
				stowTicks++;
				if (stowTicks < 2 || stowTicks > STOW_TIME/2) {
					newAccessingInventory = null;
				} else {
					newAccessingInventory = stowing == Hand.MAIN_HAND ? mainHandDir : offHandDir;
				}
			} else {
				newAccessingInventory = stowing == Hand.MAIN_HAND ? mainHandDir : offHandDir;
				Inventory out = stowing == Hand.MAIN_HAND ? mainHandOutput : offHandOutput;
				ItemStack held = stowing == Hand.MAIN_HAND ? heldItemMainHand : heldItemOffHand;
				boolean success = false;
				for (int i = 0; i < out.size(); i++) {
					if (out instanceof SidedInventory) {
						if (!((SidedInventory)out).canInsert(i, held, Direction.UP)) continue;
					}
					ItemStack there = out.getStack(i);
					if (there.isEmpty()) {
						out.setStack(i, held.copy());
						held.setCount(0);
						success = true;
						break;
					} else if (ItemStack.areItemsEqual(there, held) && ItemStack.areTagsEqual(there, held) && there.getCount()+held.getCount() <= there.getMaxCount()) {
						there.increment(held.getCount());
						out.setStack(i, there);
						held.setCount(0);
						success = true;
						break;
					}
				}
				if (success) {
					stowing = null;
					stowTicks = 0;
					sync();
					world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 0, 0);
					newAccessingInventory = null;
				}
			}
		}
		
		if (newAccessingInventory != accessingInventory) {
			cleanUpAccessingInventory();
		}
		accessingInventory = newAccessingInventory;
	}
	
	public ItemFrameEntity getItemFrame() {
		Direction facing = getCachedState().get(SkeletalSorterBlock.FACING);
		ItemFrameEntity frame = Iterables.getFirst(world.getEntitiesByClass(ItemFrameEntity.class,
				new Box(pos.up()).union(new Box(pos.up().offset(facing, 2))), null), null);
		if (frame == null) return null;
		if (frame.getHeldItemStack().isEmpty()) return null;
		RaycastContext ctx = new RaycastContext(getHeadPos(), frame.getBoundingBox().getCenter(), ShapeType.VISUAL, FluidHandling.NONE, frame) {
			@Override
			public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
				if (!state.isTranslucent(world, pos)) return super.getBlockShape(state, world, pos);
				return VoxelShapes.empty();
			}
		};
		if (world.raycast(ctx).getType() == Type.BLOCK) return null;
		return frame;
	}

	private void cleanUpAccessingInventory() {
		if (accessingInventory == null) return;
		Inventory inventory = obtainInventory(accessingInventory);
		accessingInventory = null;
		if (inventory instanceof ChestBlockEntity) {
			ChestBlockEntity cbe = (ChestBlockEntity)inventory;
			if (cbe.getCachedState().getBlock() instanceof ChestBlock) {
				world.addSyncedBlockEvent(cbe.getPos(), cbe.getCachedState().getBlock(), 1, 0);
				world.updateNeighborsAlways(cbe.getPos(), cbe.getCachedState().getBlock());
			}
		}
	}

	private Inventory obtainInventory(Direction dir) {
		BlockPos pos = this.pos.offset(dir);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof Inventory) {
			return (Inventory)be;
		}
		return null;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return YSounds.SKELETAL_SORTER_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound() {
		return YSounds.SKELETAL_SORTER_HURT;
	}
	
	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (!world.isClient) return false;
		if (type == 0) {
			stowing = data == 0 ? null : data == 1 ? Hand.MAIN_HAND : Hand.OFF_HAND;
			stowTicks = data == 0 ? 0 : 1;
			thinkTicks = 0;
			return true;
		} else if (type == 1) {
			thinkTicks = 1;
			return true;
		}
		return false;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		heldItemMainHand = ItemStack.fromTag(tag.getCompound("MainHand"));
		heldItemOffHand = ItemStack.fromTag(tag.getCompound("OffHand"));
		thinkTicks = tag.getInt("ThinkTicks");
		stowTicks = tag.getInt("StowTicks");
		stowing = tag.contains("Stowing") ? Hand.valueOf(tag.getString("Stowing")) : null;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("MainHand", heldItemMainHand.toTag(new CompoundTag()));
		tag.put("OffHand", heldItemOffHand.toTag(new CompoundTag()));
		tag.putInt("ThinkTicks", thinkTicks);
		tag.putInt("StowTicks", stowTicks);
		if (stowing != null) tag.putString("Stowing", stowing.name());
		return super.toTag(tag);
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toClientTag(super.toInitialChunkDataTag());
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		heldItemMainHand = ItemStack.fromTag(tag.getCompound("MainHand"));
		heldItemOffHand = ItemStack.fromTag(tag.getCompound("OffHand"));
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		tag.put("MainHand", heldItemMainHand.toTag(new CompoundTag()));
		tag.put("OffHand", heldItemOffHand.toTag(new CompoundTag()));
		return tag;
	}

}

package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.block.CentrifugeBlock;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YRecipeTypes;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class CentrifugeBlockEntity extends BlockEntity implements SidedInventory, Tickable, Nameable {

	private final SimpleInventory inv = new SimpleInventory(6);
	
	private int fuelTime;
	private int maxFuelTime;
	private int spinTime;
	private int maxSpinTime;
	
	private final PropertyDelegate properties = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0: return fuelTime;
				case 1: return maxFuelTime;
				case 2: return spinTime;
				case 3: return maxSpinTime;
				default: return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch(index) {
				case 0: fuelTime = value; break;
				case 1: maxFuelTime = value; break;
				case 2: spinTime = value; break;
				case 3: maxSpinTime = value; break;
			}

		}

		@Override
		public int size() {
			return 4;
		}
	};
	
	public CentrifugeBlockEntity() {
		super(YBlockEntities.CENTRIFUGE);
		
	}
	
	public PropertyDelegate getProperties() {
		return properties;
	}
	
	@Override
	public void tick() {
		boolean burning = isBurning();
		boolean needsDirty = false;
		if (fuelTime > 0) fuelTime--;

		if (!world.isClient) {
			ItemStack fuelStack = getStack(5);
			if (!isBurning() && (fuelStack.isEmpty() || getStack(0).isEmpty())) {
				if (!isBurning() && spinTime > 0) {
					spinTime = MathHelper.clamp(spinTime - 2, 0, maxSpinTime);
				}
			} else {
				CentrifugingRecipe recipe = world.getRecipeManager().getFirstMatch(YRecipeTypes.CENTRIFUGING, this, world).orElse(null);
				if (!isBurning() && recipe != null && recipe.canFitOutput(this)) {
					fuelTime = FurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuelStack.getItem(), 0);
					maxFuelTime = fuelTime;
					if (isBurning()) {
						needsDirty = true;
						if (!fuelStack.isEmpty()) {
							Item fuelItem = fuelStack.getItem();
							fuelStack.decrement(1);
							if (fuelStack.isEmpty()) {
								Item remainder = fuelItem.getRecipeRemainder();
								setStack(1, remainder == null ? ItemStack.EMPTY : new ItemStack(remainder));
							}
						}
					}
				}

				if (isBurning() && recipe != null && recipe.canFitOutput(this)) {
					maxSpinTime = recipe.getSpinTime();
					spinTime++;
					if (spinTime >= maxSpinTime) {
						spinTime = 0;
						recipe.craft(this);
						needsDirty = true;
					}
				} else {
					spinTime = 0;
				}
			}

			if (burning != isBurning()) {
				needsDirty = true;
				world.setBlockState(pos, getCachedState().with(CentrifugeBlock.LIT, isBurning()), 3);
			}
		}

		if (needsDirty) {
			markDirty();
		}
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("FuelTime", fuelTime);
		tag.putInt("MaxFuelTime", maxFuelTime);
		tag.putInt("SpinTime", spinTime);
		tag.putInt("MaxSpinTime", maxSpinTime);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		fuelTime = tag.getInt("FuelTime");
		maxFuelTime = tag.getInt("MaxFuelTime");
		spinTime = tag.getInt("SpinTime");
		maxSpinTime = tag.getInt("MaxSpinTime");
	}
	
	public boolean isBurning() {
		return fuelTime > 0;
	}

	@Override
	public int size() {
		return inv.size();
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return inv.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return inv.removeStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return inv.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inv.setStack(slot, stack);
	}
	
	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return player.squaredDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) < 8*8;
	}
	
	@Override
	public Text getName() {
		return new TranslatableText("block.yttr.centrifuge");
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return new int[] {0};
		}
		if (side == Direction.DOWN) {
			return new int[] {1, 2, 3, 4};
		}
		return new int[] {5};
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.UP) return slot == 0;
		if (dir == Direction.DOWN) return false;
		return slot == 5 && FurnaceBlockEntity.canUseAsFuel(stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN) return slot != 0 && slot != 5;
		return false;
	}

}

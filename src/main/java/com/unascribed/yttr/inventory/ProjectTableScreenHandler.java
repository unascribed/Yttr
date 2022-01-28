package com.unascribed.yttr.inventory;

import java.util.Optional;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YHandledScreens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class ProjectTableScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {
	
	private final CraftingInventory input;
	private final CraftingResultInventory result;
	private final Inventory inv;
	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	
	public ProjectTableScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, new SimpleInventory(27), playerInventory, ScreenHandlerContext.EMPTY);
	}

	public ProjectTableScreenHandler(int syncId, Inventory inv, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(YHandledScreens.PROJECT_TABLE, syncId);
		this.context = context;
		this.player = playerInventory.player;
		this.inv = inv;
		this.input = new CraftingInventory(this, 3, 3) {
			@Override
			public int size() {
				return 9;
			}

			@Override
			public boolean isEmpty() {
				for (int i = 0; i < size(); i++) {
					if (!getStack(i).isEmpty()) return false;
				}
				return true;
			}

			@Override
			public ItemStack getStack(int slot) {
				return slot >= size() ? ItemStack.EMPTY : inv.getStack(slot);
			}

			@Override
			public ItemStack removeStack(int slot) {
				if (slot >= 9) throw new IndexOutOfBoundsException();
				return inv.removeStack(slot);
			}

			@Override
			public ItemStack removeStack(int slot, int amount) {
				if (slot >= 9) throw new IndexOutOfBoundsException();
				ItemStack is = inv.removeStack(slot, amount);
				if (!is.isEmpty()) {
					onContentChanged(this);
				}
				return is;
			}

			@Override
			public void setStack(int slot, ItemStack stack) {
				if (slot >= 9) throw new IndexOutOfBoundsException();
				inv.setStack(slot, stack);
				onContentChanged(this);
			}

			@Override
			public void markDirty() {
				inv.markDirty();
			}

			@Override
			public boolean canPlayerUse(PlayerEntity player) {
				return inv.canPlayerUse(player);
			}

			@Override
			public void clear() {
				for (int i = 0; i < size(); i++) {
					removeStack(i);
				}
			}
		};
		result = new CraftingResultInventory();
		
		addSlot(new CraftingResultSlot(player, input, result, 0, 124, 35));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new Slot(input, x + y * 3, 30 + x * 18, 17 + y * 18));
			}
		}

		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 133 + y * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 191));
		}
		
		updateResult(syncId, player.world, player, input, result);

	}

	protected static void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
		if (!world.isClient) {
			ServerPlayerEntity spe = (ServerPlayerEntity)player;
			ItemStack res = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
			if (optional.isPresent()) {
				CraftingRecipe craftingRecipe = optional.get();
				if (resultInventory.shouldCraftRecipe(world, spe, craftingRecipe)) {
					res = craftingRecipe.craft(craftingInventory);
				}
			}

			resultInventory.setStack(0, res);
			spe.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, 0, res));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		context.run((world, blockPos) -> {
			updateResult(syncId, world, player, input, result);
		});
	}

	@Override
	public void populateRecipeFinder(RecipeMatcher finder) {
		for (int i = 0; i < inv.size(); i++) {
			finder.addUnenchantedInput(inv.getStack(i));
		}
	}
	
	@Override
	public void fillInputSlots(boolean craftAll, Recipe<?> recipe, ServerPlayerEntity player) {
		new InputSlotFiller<CraftingInventory>(this) {
			@Override
			protected void fillInputSlot(Slot slot, ItemStack stack) {
				// scan storage, but not grid
				for (int i = 9; i < inv.size(); ++i) {
					ItemStack is = inv.getStack(i);
					if (!is.isEmpty() && ItemStack.areItemsEqual(stack, is) && !(is.isDamaged() && !is.hasEnchantments() && !is.hasCustomName())) {
						is = is.copy();
						if (is.getCount() > 1) {
							inv.removeStack(i, 1);
						} else {
							inv.removeStack(i);
						}

						is.setCount(1);
						if (slot.getStack().isEmpty()) {
							slot.setStack(is);
						} else {
							slot.getStack().increment(1);
						}
						return;
					}
				}
				// handle player inventory
				super.fillInputSlot(slot, stack);
			}
			
			@Override
			protected void returnSlot(int slotId) {
				if (slotId == 0) return;
				Slot slot = getSlot(slotId);
				ItemStack is = slot.getStack();
				is = tryTransfer(slot, is, inv, 9);
				is = tryTransfer(slot, is, player.inventory, 0);
			}

			private ItemStack tryTransfer(Slot from, ItemStack is, Inventory inv, int start) {
				for (int i = start; i < inv.size(); i++) {
					if (is.isEmpty()) return is;
					ItemStack there = inv.getStack(i);
					if (there.isEmpty() || canStacksCombine(is, there) && inv.isValid(i, is)) {
						int canTransfer = there.getMaxCount()-there.getCount();
						if (canTransfer > 0) {
							if (there.isEmpty()) {
								inv.setStack(i, is.copy());
								from.setStack(ItemStack.EMPTY);
								return ItemStack.EMPTY;
							} else {
								int transfer = Math.min(canTransfer, is.getCount());
								there.increment(transfer);
								inv.setStack(i, there);
								is.decrement(transfer);
								from.setStack(is);
							}
						}
					}
				}
				return is;
			}
		}.fillInputSlots(player, (Recipe<CraftingInventory>)recipe, craftAll);
	}

	@Override
	public void clearCraftingSlots() {
		input.clear();
		result.clear();
	}

	@Override
	public boolean matches(Recipe<? super CraftingInventory> recipe) {
		return recipe.matches(input, player.world);
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(context, player, YBlocks.PROJECT_TABLE);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 0) {
				this.context.run((world, blockPos) -> {
					itemStack2.getItem().onCraft(itemStack2, world, player);
				});
				if (!this.insertItem(itemStack2, 28, 63, false)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(itemStack2, itemStack);
			} else if (index >= 28 && index <= 63) {
				if (!this.insertItem(itemStack2, 10, 27, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 28, 63, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemStack3 = slot.onTakeItem(player, itemStack2);
			if (index == 0) {
				player.dropItem(itemStack3, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.result && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return this.input.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.input.getHeight();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getCraftingSlotCount() {
		return 9;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public RecipeBookCategory getCategory() {
		return RecipeBookCategory.CRAFTING;
	}
}

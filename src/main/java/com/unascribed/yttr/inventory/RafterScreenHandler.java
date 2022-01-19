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
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
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

public class RafterScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {
	public static class FloatingSlot extends Slot {

		public float floatingX, floatingY, ang;
		
		public FloatingSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}
		
		@Override
		public boolean doDrawHoveringEffect() {
			return false;
		}
		
	}
	
	private final CraftingInventory input;
	private final CraftingResultInventory result;
	private final ScreenHandlerContext context;
	private final PlayerEntity player;

	public RafterScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public RafterScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(YHandledScreens.RAFTING, syncId);
		this.input = new CraftingInventory(this, 6, 6);
		this.result = new CraftingResultInventory();
		this.context = context;
		this.player = playerInventory.player;
		this.addSlot(new CraftingResultSlot(playerInventory.player, input, result, 0, 149, 73));

		for(int x = 0; x < 6; ++x) {
			for(int y = 0; y < 6; ++y) {
				if (x <= 1 || y <= 1) {
					addSlot(new FloatingSlot(input, y + x * 6, 0, 0));
				} else {
					addSlot(new Slot(input, y + x * 6, y * 18 + 1, x * 18 + 1));
				}
			}
		}

		for(int y = 0; y < 3; ++y) {
			for(int x = 0; x < 9; ++x) {
				addSlot(new Slot(playerInventory, x + y * 9 + 9, 33 + x * 18, 122 + y * 18));
			}
		}

		for(int i = 0; i < 9; ++i) {
			addSlot(new Slot(playerInventory, i, 33 + i * 18, 180));
		}

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
		input.provideRecipeInputs(finder);
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
		context.run((world, blockPos) -> {
			dropInventory(player, world, input);
		});
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(context, player, YBlocks.RAFTER);
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
				if (!this.insertItem(itemStack2, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(itemStack2, itemStack);
			} else if (index >= 10 && index < 46) {
				if (!this.insertItem(itemStack2, 1, 10, false)) {
					if (index < 37) {
						if (!this.insertItem(itemStack2, 37, 46, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.insertItem(itemStack2, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.insertItem(itemStack2, 10, 46, false)) {
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
		return 10;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public RecipeBookCategory getCategory() {
		return RecipeBookCategory.CRAFTING;
	}
}

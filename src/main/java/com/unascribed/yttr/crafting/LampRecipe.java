package com.unascribed.yttr.crafting;

import java.util.Locale;

import com.unascribed.yttr.block.decor.LampBlock;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.mixin.accessor.AccessorShapedRecipe;

import com.google.common.base.Enums;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class LampRecipe extends ShapedRecipe {

	public LampRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output) {
		super(id, group, width, height, ingredients, output);
	}
	
	public LampRecipe(ShapedRecipe copy) {
		this(copy.getId(), ((AccessorShapedRecipe)copy).yttr$getGroup(), copy.getWidth(), copy.getHeight(), copy.getPreviewInputs(), copy.getOutput());
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		return super.matches(inv, world) && !craft(inv).isEmpty();
	}
	
	@Override
	public ItemStack craft(CraftingInventory inv) {
		ItemStack stack = getOutput().copy();
		boolean containsTorch = false;
		Boolean inputLampInverted = null;
		LampColor inputLampColor = null;
		LampColor color = null;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack in = inv.getStack(i);
			if (in.getItem() == Items.REDSTONE_TORCH) {
				containsTorch = true;
			} else if (in.getItem() instanceof BlockItem && ((BlockItem)in.getItem()).getBlock() instanceof LampBlock) {
				boolean thisInverted = LampBlockItem.isInverted(in);
				LampColor thisColor = LampBlockItem.getColor(in);
				if (inputLampInverted != null && inputLampInverted != thisInverted) return ItemStack.EMPTY;
				if (inputLampColor != null && inputLampColor != thisColor) return ItemStack.EMPTY;
				inputLampColor = thisColor;
				inputLampInverted = thisInverted;
			} else {
				Identifier id = Registry.ITEM.getId(in.getItem());
				String path = id.getPath();
				if (id.getNamespace().equals("minecraft") && path.endsWith("_dye")) {
					LampColor thisColor = Enums.getIfPresent(LampColor.class, path.substring(0, path.length()-4).toUpperCase(Locale.ROOT)).orNull();
					if (color != null && color != thisColor) return ItemStack.EMPTY;
					if (thisColor != null) color = thisColor;
				}
			}
		}
		LampBlockItem.setInverted(stack, inputLampInverted == null ? containsTorch : inputLampInverted ^ containsTorch);
		LampBlockItem.setColor(stack, color == null ? inputLampColor == null ? LampColor.COLORLESS : inputLampColor : color);
		return stack;
	}

}

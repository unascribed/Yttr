package com.unascribed.yttr.crafting;

import java.util.List;
import com.unascribed.yttr.block.decor.LampBlock;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.mixin.accessor.AccessorShapedRecipe;
import com.unascribed.yttr.util.Resolvable;

import com.google.common.collect.Lists;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class LampRecipe extends ShapedRecipe {

	private final List<String> stripTags = Lists.newArrayList();
	
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
				Item item = in.getItem();
				LampColor thisColor = null;
				if (item instanceof DyeItem) {
					thisColor = LampColor.BY_DYE.get(((DyeItem)item).getColor());
				} else {
					thisColor = LampColor.BY_ITEM.get(Resolvable.mapKey(item, Registry.ITEM));
				}
				if (color != null && color != thisColor) return ItemStack.EMPTY;
				if (thisColor != null) color = thisColor;
			}
		}
		LampBlockItem.setInverted(stack, inputLampInverted == null ? containsTorch : inputLampInverted ^ containsTorch);
		LampBlockItem.setColor(stack, color == null ? inputLampColor == null ? LampColor.COLORLESS : inputLampColor : color);
		for (String s : stripTags) {
			stack.getTag().remove(s);
		}
		return stack;
	}

	public void addStripTag(String tag) {
		stripTags.add(tag);
	}
	
	public List<String> getStripTags() {
		return stripTags;
	}

}

package com.unascribed.yttr;

import java.util.List;

import com.google.gson.internal.UnsafeAllocator;
import com.unascribed.yttr.mixinsupport.ItemGroupParent;

import com.google.common.collect.Lists;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class ItemSubGroup extends ItemGroup {

	private int index;
	private String id;
	private ItemGroup parent;
	private Text translationKey;
	private List<ItemStack> additionalStacks;
	
	private ItemSubGroup() {
		// NOT CALLED
		super(-1, null);
	}
	
	public static ItemSubGroup create(ItemGroup parent, Identifier id) {
		try {
			ItemGroupParent igp = (ItemGroupParent)parent;
			ItemSubGroup isg = UnsafeAllocator.create().newInstance(ItemSubGroup.class);
			isg.index = igp.yttr$getChildren().size();
			isg.id = id.getNamespace()+"."+id.getPath();
			isg.parent = parent;
			isg.translationKey = new TranslatableText("itemGroup."+isg.id);
			isg.additionalStacks = Lists.newArrayList();
			igp.yttr$getChildren().add(isg);
			if (igp.yttr$getSelectedChild() == null) {
				igp.yttr$setSelectedChild(isg);
			}
			return isg;
		} catch (Exception e) {
			throw new Error(e);
		}
	}
	
	@Override
	public void appendStacks(DefaultedList<ItemStack> stacks) {
		super.appendStacks(stacks);
		stacks.addAll(additionalStacks);
	}
	
	@Override
	public int getIndex() {
		return index;
	}
	
	public String getId() {
		return id;
	}
	
	public ItemGroup getParent() {
		return parent;
	}

	@Override
	public EnchantmentTarget[] getEnchantments() {
		return new EnchantmentTarget[0];
	}
	
	@Override
	public Text getTranslationKey() {
		return translationKey;
	}
	
	@Override
	public ItemStack createIcon() {
		return ItemStack.EMPTY;
	}
	
}

package com.unascribed.yttr.mixin.subgroup;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.ItemSubGroup;
import com.unascribed.yttr.mixinsupport.ItemGroupParent;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ItemGroup.class)
public class MixinItemGroup implements ItemGroupParent {

	private final List<ItemSubGroup> yttr$children = Lists.newArrayList();
	private ItemSubGroup yttr$selectedChild = null;
	
	@Inject(at=@At("HEAD"), method="appendStacks", cancellable=true)
	public void appendStacksHead(DefaultedList<ItemStack> stacks, CallbackInfo ci) {
		if (yttr$selectedChild != null) {
			yttr$selectedChild.appendStacks(stacks);
			ci.cancel();
		}
	}
	
	@Inject(at=@At("TAIL"), method="appendStacks", cancellable=true)
	public void appendStacksTail(DefaultedList<ItemStack> stacks, CallbackInfo ci) {
		if (yttr$children != null) {
			for (ItemSubGroup child : yttr$children) {
				child.appendStacks(stacks);
			}
		}
	}
	
	@Override
	public List<ItemSubGroup> yttr$getChildren() {
		return yttr$children;
	}
	
	@Override
	public ItemSubGroup yttr$getSelectedChild() {
		return yttr$selectedChild;
	}
	
	@Override
	public void yttr$setSelectedChild(ItemSubGroup group) {
		yttr$selectedChild = group;
	}
	
}

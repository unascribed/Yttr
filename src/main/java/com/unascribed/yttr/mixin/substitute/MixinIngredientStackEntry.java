package com.unascribed.yttr.mixin.substitute;

import java.util.Collection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Substitutes;
import com.unascribed.yttr.mixinsupport.SetNoSubstitution;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;

@Mixin(targets="net/minecraft/recipe/Ingredient$StackEntry")
public class MixinIngredientStackEntry implements SetNoSubstitution {

	@Shadow @Final
	private ItemStack stack;
	
	private boolean yttr$noSubstitution;
	
	@Inject(at=@At("HEAD"), method="getStacks", cancellable=true)
	public void getStacks(CallbackInfoReturnable<Collection<ItemStack>> ci) {
		if (!yttr$noSubstitution && Substitutes.getSubstitute(stack.getItem()) != null) {
			ci.setReturnValue(Lists.newArrayList(stack, Substitutes.sub(stack)));
		}
	}
	
	@Override
	public void yttr$setNoSubstitution(boolean b) {
		this.yttr$noSubstitution = b;
	}
	
}

package com.unascribed.yttr.mixin.substitute;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.unascribed.yttr.Substitutes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

@Mixin(Ingredient.class)
public abstract class MixinIngredient {
	
	@Shadow @Final
	private Ingredient.Entry[] entries;
	
	@Shadow
	public abstract boolean test(ItemStack itemStack);
	
	@Inject(at=@At("RETURN"), method="test", cancellable=true)
	public void test(@Nullable ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (itemStack != null && !ci.getReturnValueZ()) {
			if (Substitutes.getPrime(itemStack.getItem()) != null) {
				ci.setReturnValue(test(Substitutes.prime(itemStack)));
			}
		}
	}
	
}

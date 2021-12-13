package com.unascribed.yttr.mixin.curse;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.init.YEnchantments;
import com.unascribed.yttr.init.YRecipeTypes;

@Mixin(Block.class)
public class MixinBlock {

	private static CraftingInventory yttr$inv = new CraftingInventory(new ScreenHandler(null, -1) {
		@Override
		public boolean canUse(PlayerEntity player) {
			return false;
		}
	}, 1, 1);
	private static boolean yttr$shattering;
	private static int yttr$shatteringDepth;
	
	@Inject(at=@At("HEAD"), method="dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
			cancellable=true)
	private static void dropStacksHead(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
		if (EnchantmentHelper.getLevel(YEnchantments.ANNIHILATION_CURSE, stack) > 0) {
			ci.cancel();
			return;
		}
		yttr$shatteringDepth = 0;
		yttr$shattering = EnchantmentHelper.getLevel(YEnchantments.SHATTERING_CURSE, stack) > 0;
	}

	@Inject(at=@At("RETURN"), method="dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V")
	private static void dropStacksTail(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
		yttr$shattering = false;
	}
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/world/World.spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals=LocalCapture.CAPTURE_FAILHARD, method="dropStack", cancellable=true)
	private static void dropStack(World world, BlockPos pos, ItemStack stack, CallbackInfo ci, float f, double x, double y, double z, ItemEntity entity) {
		if (yttr$shattering) {
			if (yttr$shatteringDepth > 0) {
				entity.setVelocity(entity.getPos().subtract(Vec3d.ofCenter(pos)).normalize().multiply(0.2));
				if (ThreadLocalRandom.current().nextInt(10*yttr$shatteringDepth) != 0) {
					return;
				}
			}
			for (int j = 0; j < stack.getCount(); j++) {
				ItemStack copy1 = stack.copy();
				copy1.setCount(1);
				yttr$inv.setStack(0, copy1);
				Optional<? extends Recipe<CraftingInventory>> recipe = world.getRecipeManager().getFirstMatch(YRecipeTypes.SHATTERING, yttr$inv, world);
				List<ItemStack> remainder = null;
				if (!recipe.isPresent()) {
					recipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, yttr$inv, world);
					remainder = world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, yttr$inv, world);
				}
				if (!recipe.isPresent()) {
					for (StonecuttingRecipe sr : world.getRecipeManager().listAllOfType(RecipeType.STONECUTTING)) {
						if (sr.getOutput().getCount() == 1 && ItemStack.areEqual(sr.getOutput(), copy1)) {
							ItemStack input = new ItemStack(Item.byRawId(sr.getIngredients().get(0).getMatchingItemIds().getInt(0)));
							recipe = Optional.of(new ShapelessRecipe(sr.getId(), null,
									input, DefaultedList.ofSize(1, Ingredient.ofStacks(sr.getOutput()))));
							remainder = null;
							break;
						}
					}
				}
				if (!recipe.isPresent()) return;
				ItemStack result = recipe.get().craft(yttr$inv);
				try {
					yttr$shatteringDepth++;
					for (int i = 0; i < result.getCount(); i++) {
						ItemStack copy = result.copy();
						copy.setCount(1);
						dropStack(world, pos, copy);
					}
					if (remainder != null) {
						for (ItemStack is : remainder) {
							for (int i = 0; i < is.getCount(); i++) {
								ItemStack copy = is.copy();
								copy.setCount(1);
								dropStack(world, pos, copy);
							}
						}
					}
					ci.cancel();
				} finally {
					yttr$shatteringDepth--;
				}
			}
		}
	}
	
	@Shadow
	public static void dropStack(World world, BlockPos pos, ItemStack stack) { }
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/entity/ExperienceOrbEntity.roundToOrbSize(I)I"),
			method="dropExperience", ordinal=1)
	protected int dropExperience(int orig) {
		if (yttr$shattering) {
			return Math.min(orig, 3);
		}
		return orig;
	}
	
}

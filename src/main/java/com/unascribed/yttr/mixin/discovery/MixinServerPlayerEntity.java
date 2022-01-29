package com.unascribed.yttr.mixin.discovery;

import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.unascribed.yttr.Yttr;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(at=@At("HEAD"), method="onSlotUpdate")
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack, CallbackInfo ci) {
		if (!(handler.getSlot(slotId) instanceof CraftingResultSlot) && handler == playerScreenHandler) {
			Identifier id = Registry.ITEM.getId(stack.getItem());
			unlockRecipes(Yttr.discoveries.get(id).stream()
				.map(world.getRecipeManager()::get)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList()));
		}
	}

}

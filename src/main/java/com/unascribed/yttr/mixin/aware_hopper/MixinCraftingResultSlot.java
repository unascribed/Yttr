package com.unascribed.yttr.mixin.aware_hopper;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.abomination.AwareHopperBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot {

	@Shadow @Final
	private CraftingInventory input;
	@Shadow @Final
	private PlayerEntity player;
	
	@Inject(at=@At("HEAD"), method="onCrafted(Lnet/minecraft/item/ItemStack;)V")
	protected void onCrafted(ItemStack stack, CallbackInfo ci) {
		if (player == null) return;
		if (player.world.isClient) return;
		ChunkPos cp = new ChunkPos(player.getBlockPos());
		for (int cX = -1; cX <= 1; cX++) {
			for (int cZ = -1; cZ <= 1; cZ++) {
				WorldChunk c = player.world.getChunk(cp.x+cX, cp.z+cZ);
				for (BlockEntity be : c.getBlockEntities().values()) {
					if (be instanceof AwareHopperBlockEntity) {
						AwareHopperBlockEntity ah = (AwareHopperBlockEntity)be;
						ah.onNearbyCrafting(player, input);
					}
				}
			}
		}
	}
	
}

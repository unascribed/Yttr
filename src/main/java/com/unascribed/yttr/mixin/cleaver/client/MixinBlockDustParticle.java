package com.unascribed.yttr.mixin.cleaver.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
@Mixin(BlockDustParticle.class)
public abstract class MixinBlockDustParticle extends SpriteBillboardParticle {

	protected MixinBlockDustParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f);
	}
	
	@Inject(at=@At("HEAD"), method="updateColor")
	protected void updateColor(@Nullable BlockPos bp, CallbackInfo ci) {
		if (bp != null) {
			BlockEntity be = world.getBlockEntity(bp);
			if (be instanceof CleavedBlockEntity) {
				setSprite(MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getSprite(((CleavedBlockEntity) be).getDonor()));
			}
		}
	}

}

package com.unascribed.yttr.mixin.ibxm;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.IBXMAudioStream;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.RepeatingAudioStream;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Mixin(SoundLoader.class)
public class MixinSoundLoader {

	@Shadow @Final
	private ResourceManager resourceManager;

	@Inject(at=@At("HEAD"), method="loadStreamed", cancellable=true)
	public void loadStreamed(Identifier id, boolean repeatInstantly, CallbackInfoReturnable<CompletableFuture<AudioStream>> ci) {
		if (id.getPath().endsWith(".yttr_xm") || id.getPath().endsWith(".yttr_s3m") || id.getPath().endsWith(".yttr_mod")) {
			ci.setReturnValue(CompletableFuture.supplyAsync(() -> {
				try {
					Resource resource = this.resourceManager.getResource(id);
					InputStream inputStream = resource.getInputStream();
					return repeatInstantly ? new RepeatingAudioStream(IBXMAudioStream::new, inputStream) : new IBXMAudioStream(inputStream);
				} catch (IOException var5) {
					throw new CompletionException(var5);
				}
			}, Util.getMainWorkerExecutor()));
		}
	}

}

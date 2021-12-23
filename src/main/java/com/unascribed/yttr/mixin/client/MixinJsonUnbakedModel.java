package com.unascribed.yttr.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.InheritElements;

import com.google.common.collect.Lists;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;

@Environment(EnvType.CLIENT)
@Mixin(JsonUnbakedModel.class)
public class MixinJsonUnbakedModel implements InheritElements {

	@Shadow
	protected JsonUnbakedModel parent;
	@Shadow @Final
	private List<ModelElement> elements;
	
	private boolean yttr$inheritElements;
	
	@Inject(at=@At("HEAD"), method="getElements", cancellable=true)
	public void getElements(CallbackInfoReturnable<List<ModelElement>> ci) {
		if (yttr$inheritElements && parent != null && !elements.isEmpty()) {
			List<ModelElement> li = Lists.newArrayListWithCapacity(elements.size()+parent.getElements().size());
			li.addAll(parent.getElements());
			li.addAll(elements);
			ci.setReturnValue(li);
		}
	}
	
	@Override
	public boolean yttr$isInheritElements() {
		return yttr$inheritElements;
	}

	@Override
	public void yttr$setInheritElements(boolean b) {
		yttr$inheritElements = b;
	}

	
}

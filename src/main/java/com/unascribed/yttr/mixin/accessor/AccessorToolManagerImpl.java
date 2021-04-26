package com.unascribed.yttr.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;
import net.minecraft.block.Block;

@Mixin(value=ToolManagerImpl.class, remap=false)
public interface AccessorToolManagerImpl {

	@Accessor("ENTRIES")
	Map<Block, ? extends ToolManagerImpl.Entry> yttr$getEntries();
	
}

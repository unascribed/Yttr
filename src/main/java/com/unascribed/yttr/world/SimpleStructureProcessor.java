package com.unascribed.yttr.world;

import net.minecraft.structure.StructurePlacementData;

import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class SimpleStructureProcessor extends StructureProcessor {

	public interface Delegate {
		StructureBlockInfo process(StructureBlockInfo info);
	}
	
	private final Delegate delegate;
	
	private SimpleStructureProcessor(Delegate delegate) {
		this.delegate = delegate;
	}

	public static SimpleStructureProcessor of(Delegate delegate) {
		return new SimpleStructureProcessor(delegate);
	}

	@Override
	public StructureBlockInfo process(WorldView world,
			BlockPos unk, BlockPos unk2,
			StructureBlockInfo unk3, StructureBlockInfo block,
			StructurePlacementData structurePlacementData) {
		return delegate.process(block);
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.NOP;
	}
	
}

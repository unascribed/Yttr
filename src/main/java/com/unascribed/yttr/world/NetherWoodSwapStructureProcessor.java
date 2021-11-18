package com.unascribed.yttr.world;

import com.google.common.collect.ImmutableBiMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class NetherWoodSwapStructureProcessor extends StructureProcessor {
	
	private static final ImmutableBiMap<Block, Block> SWAP = ImmutableBiMap.<Block, Block>builder()
			.put(Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON)
			.put(Blocks.CRIMSON_DOOR, Blocks.WARPED_DOOR)
			.put(Blocks.CRIMSON_FENCE, Blocks.WARPED_FENCE)
			.put(Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE)
			.put(Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS)
			.put(Blocks.CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE)
			.put(Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM)
			.put(Blocks.CRIMSON_PLANKS, Blocks.WARPED_PLANKS)
			.put(Blocks.CRIMSON_PRESSURE_PLATE, Blocks.WARPED_PRESSURE_PLATE)
			.put(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS)
			.put(Blocks.CRIMSON_SIGN, Blocks.WARPED_SIGN)
			.put(Blocks.CRIMSON_SLAB, Blocks.WARPED_SLAB)
			.put(Blocks.CRIMSON_STAIRS, Blocks.WARPED_STAIRS)
			.put(Blocks.CRIMSON_STEM, Blocks.WARPED_STEM)
			.put(Blocks.CRIMSON_TRAPDOOR, Blocks.WARPED_TRAPDOOR)
			.put(Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_WALL_SIGN)
			.put(Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
			.put(Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_WARPED_STEM)
			.put(Blocks.POTTED_CRIMSON_FUNGUS, Blocks.POTTED_WARPED_FUNGUS)
			.put(Blocks.POTTED_CRIMSON_ROOTS, Blocks.POTTED_WARPED_ROOTS)
			.build();
	
	private final boolean warped;

	public NetherWoodSwapStructureProcessor(boolean warped) {
		this.warped = warped;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public StructureBlockInfo process(WorldView world,
			BlockPos unk, BlockPos unk2,
			StructureBlockInfo unk3, StructureBlockInfo block,
			StructurePlacementData structurePlacementData) {
		if (warped) {
			Block swapped = SWAP.getOrDefault(block.state.getBlock(), SWAP.inverse().get(block.state.getBlock()));
			if (swapped != null) {
				BlockState bs = swapped.getDefaultState();
				for (Property prop : block.state.getProperties()) {
					if (bs.contains(prop)) {
						bs = bs.with(prop, block.state.get(prop));
					}
				}
				return new StructureBlockInfo(block.pos, bs, block.tag);
			}
		}
		return block;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.NOP;
	}
}
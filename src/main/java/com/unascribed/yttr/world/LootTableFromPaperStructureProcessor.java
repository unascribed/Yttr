package com.unascribed.yttr.world;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class LootTableFromPaperStructureProcessor extends StructureProcessor {

	@Override
	public StructureBlockInfo process(WorldView world,
			BlockPos unk, BlockPos unk2,
			StructureBlockInfo unk3, StructureBlockInfo block,
			StructurePlacementData structurePlacementData) {
		if (block.tag != null && block.tag.contains("Items")) {
			NbtList items = block.tag.getList("Items", NbtType.COMPOUND);
			if (items.size() == 1) {
				ItemStack item = ItemStack.fromNbt(items.getCompound(0));
				if (item.getItem() == Items.PAPER && item.hasCustomName()) {
					Identifier id = new Identifier(item.getName().asString());
					Identifier finId = new Identifier(id.getNamespace(), "chests/"+id.getPath());
					NbtCompound newTag = block.tag.copy();
					newTag.remove("Items");
					newTag.putString("LootTable", finId.toString());
					return new StructureBlockInfo(block.pos, block.state, newTag);
				}
			}
		}
		return block;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.NOP;
	}
}
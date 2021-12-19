package com.unascribed.yttr.mixin.convenience;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

@Mixin(AbstractBlock.class)
public class MixinAbstractBlock {

	@Inject(at=@At("RETURN"), method="getDroppedStacks", cancellable=true)
	public void getDroppedStacks(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> ci) {
		if (ci.getReturnValue().isEmpty()) {
			AbstractBlock self = (AbstractBlock)(Object)this;
			Identifier id = self.getLootTableId();
			if (id == LootTables.EMPTY) {
				return;
			}
			LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
			ServerWorld serverWorld = lootContext.getWorld();
			LootTable lootTable = serverWorld.getServer().getLootManager().getTable(id);
			if (lootTable == LootTable.EMPTY && "yttr".equals(id.getNamespace()) && self.asItem() != Items.AIR) {
				ci.setReturnValue(ImmutableList.of(new ItemStack(self.asItem())));
			}
		}
	}
	
}

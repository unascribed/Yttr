package com.unascribed.yttr.init;

import java.util.function.Supplier;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.annotate.Renderer;
import com.unascribed.yttr.block.entity.AwareHopperBlockEntity;
import com.unascribed.yttr.block.entity.ChuteBlockEntity;
import com.unascribed.yttr.block.entity.CleavedBlockEntity;
import com.unascribed.yttr.block.entity.LampBlockEntity;
import com.unascribed.yttr.block.entity.LevitationChamberBlockEntity;
import com.unascribed.yttr.block.entity.PowerMeterBlockEntity;
import com.unascribed.yttr.block.entity.SqueezedLeavesBlockEntity;
import com.unascribed.yttr.block.entity.VoidGeyserBlockEntity;
import com.unascribed.yttr.client.render.AwareHopperBlockEntityRenderer;
import com.unascribed.yttr.client.render.CleavedBlockEntityRenderer;
import com.unascribed.yttr.client.render.LampBlockEntityRenderer;
import com.unascribed.yttr.client.render.LevitationChamberBlockEntityRenderer;
import com.unascribed.yttr.client.render.PowerMeterBlockEntityRenderer;
import com.unascribed.yttr.client.render.SqueezedLeavesBlockEntityRenderer;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class YBlockEntities {

	@Renderer(AwareHopperBlockEntityRenderer.class)
	public static final BlockEntityType<AwareHopperBlockEntity> AWARE_HOPPER = create(AwareHopperBlockEntity::new, YBlocks.AWARE_HOPPER);
	@Renderer(PowerMeterBlockEntityRenderer.class)
	public static final BlockEntityType<PowerMeterBlockEntity> POWER_METER = create(PowerMeterBlockEntity::new, YBlocks.POWER_METER);
	@Renderer(LevitationChamberBlockEntityRenderer.class)
	public static final BlockEntityType<LevitationChamberBlockEntity> LEVITATION_CHAMBER = create(LevitationChamberBlockEntity::new, YBlocks.LEVITATION_CHAMBER);
	public static final BlockEntityType<ChuteBlockEntity> CHUTE = create(ChuteBlockEntity::new, YBlocks.CHUTE);
	public static final BlockEntityType<VoidGeyserBlockEntity> VOID_GEYSER = create(VoidGeyserBlockEntity::new, YBlocks.VOID_GEYSER);
	@Renderer(SqueezedLeavesBlockEntityRenderer.class)
	public static final BlockEntityType<SqueezedLeavesBlockEntity> SQUEEZED_LEAVES = create(SqueezedLeavesBlockEntity::new, YBlocks.SQUEEZED_LEAVES);
	@Renderer(LampBlockEntityRenderer.class)
	public static final BlockEntityType<LampBlockEntity> LAMP = create(LampBlockEntity::new, YBlocks.LAMP, YBlocks.FIXTURE, YBlocks.CAGE_LAMP);
	@Renderer(CleavedBlockEntityRenderer.class)
	public static final BlockEntityType<CleavedBlockEntity> CLEAVED_BLOCK = create(CleavedBlockEntity::new, YBlocks.CLEAVED_BLOCK);
	
	private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> cons, Block... acceptableBlocks) {
		return new BlockEntityType<>(cons, ImmutableSet.copyOf(acceptableBlocks), null);
	}

	public static void init() {
		Yttr.autoRegister(Registry.BLOCK_ENTITY_TYPE, YBlockEntities.class, BlockEntityType.class);
	}

}

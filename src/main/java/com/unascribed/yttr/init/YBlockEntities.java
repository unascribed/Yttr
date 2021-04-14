package com.unascribed.yttr.init;

import java.util.function.Supplier;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.block.abomination.AwareHopperBlockEntity;
import com.unascribed.yttr.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.block.decor.LampBlockEntity;
import com.unascribed.yttr.block.device.CentrifugeBlockEntity;
import com.unascribed.yttr.block.device.PowerMeterBlockEntity;
import com.unascribed.yttr.block.device.SuitStationBlockEntity;
import com.unascribed.yttr.block.mechanism.ChuteBlockEntity;
import com.unascribed.yttr.block.mechanism.DopperBlockEntity;
import com.unascribed.yttr.block.mechanism.FlopperBlockEntity;
import com.unascribed.yttr.block.mechanism.LevitationChamberBlockEntity;
import com.unascribed.yttr.block.squeeze.SqueezedLeavesBlockEntity;
import com.unascribed.yttr.block.void_.VoidGeyserBlockEntity;
import com.unascribed.yttr.client.render.block_entity.AwareHopperBlockEntityRenderer;
import com.unascribed.yttr.client.render.block_entity.CleavedBlockEntityRenderer;
import com.unascribed.yttr.client.render.block_entity.LampBlockEntityRenderer;
import com.unascribed.yttr.client.render.block_entity.LevitationChamberBlockEntityRenderer;
import com.unascribed.yttr.client.render.block_entity.PowerMeterBlockEntityRenderer;
import com.unascribed.yttr.client.render.block_entity.SqueezedLeavesBlockEntityRenderer;
import com.unascribed.yttr.util.annotate.Renderer;

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
	public static final BlockEntityType<CentrifugeBlockEntity> CENTRIFUGE = create(CentrifugeBlockEntity::new, YBlocks.CENTRIFUGE);
	public static final BlockEntityType<DopperBlockEntity> DOPPER = create(DopperBlockEntity::new, YBlocks.DOPPER);
	public static final BlockEntityType<FlopperBlockEntity> FLOPPER = create(FlopperBlockEntity::new, YBlocks.FLOPPER);
	public static final BlockEntityType<SuitStationBlockEntity> SUIT_STATION = create(SuitStationBlockEntity::new, YBlocks.SUIT_STATION);
	
	private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> cons, Block... acceptableBlocks) {
		return new BlockEntityType<>(cons, ImmutableSet.copyOf(acceptableBlocks), null);
	}

	public static void init() {
		Yttr.autoRegister(Registry.BLOCK_ENTITY_TYPE, YBlockEntities.class, BlockEntityType.class);
	}

}

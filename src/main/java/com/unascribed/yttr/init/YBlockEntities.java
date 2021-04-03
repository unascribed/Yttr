package com.unascribed.yttr.init;

import java.util.function.Supplier;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.block.entity.AwareHopperBlockEntity;
import com.unascribed.yttr.block.entity.ChuteBlockEntity;
import com.unascribed.yttr.block.entity.LampBlockEntity;
import com.unascribed.yttr.block.entity.LevitationChamberBlockEntity;
import com.unascribed.yttr.block.entity.PowerMeterBlockEntity;
import com.unascribed.yttr.block.entity.SqueezedLeavesBlockEntity;
import com.unascribed.yttr.block.entity.VoidGeyserBlockEntity;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class YBlockEntities {

	public static final BlockEntityType<AwareHopperBlockEntity> AWARE_HOPPER = create(AwareHopperBlockEntity::new, YBlocks.AWARE_HOPPER);
	public static final BlockEntityType<PowerMeterBlockEntity> POWER_METER = create(PowerMeterBlockEntity::new, YBlocks.POWER_METER);
	public static final BlockEntityType<LevitationChamberBlockEntity> LEVITATION_CHAMBER = create(LevitationChamberBlockEntity::new, YBlocks.LEVITATION_CHAMBER);
	public static final BlockEntityType<ChuteBlockEntity> CHUTE = create(ChuteBlockEntity::new, YBlocks.CHUTE);
	public static final BlockEntityType<VoidGeyserBlockEntity> VOID_GEYSER = create(VoidGeyserBlockEntity::new, YBlocks.VOID_GEYSER);
	public static final BlockEntityType<SqueezedLeavesBlockEntity> SQUEEZED_LEAVES = create(SqueezedLeavesBlockEntity::new, YBlocks.SQUEEZED_LEAVES);
	public static final BlockEntityType<LampBlockEntity> LAMP = create(LampBlockEntity::new, YBlocks.LAMP, YBlocks.FIXTURE, YBlocks.CAGE_LAMP);
	
	private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> cons, Block... acceptableBlocks) {
		return new BlockEntityType<>(cons, ImmutableSet.copyOf(acceptableBlocks), null);
	}

	public static void init() {
		Yttr.autoRegister(Registry.BLOCK_ENTITY_TYPE, YBlockEntities.class, BlockEntityType.class);
	}

}

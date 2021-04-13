package com.unascribed.yttr.block;

import com.unascribed.yttr.SuitResource;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.block.entity.SuitStationBlockEntity;
import com.unascribed.yttr.item.SuitArmorItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SuitStationBlock extends Block implements BlockEntityProvider {

	private static final VoxelShape COLLISION_SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 0.99, 1);
	
	public SuitStationBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SuitStationBlockEntity();
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION_SHAPE;
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.isClient) return;
		if (entity instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)entity;
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof SuitStationBlockEntity) {
				SuitStationBlockEntity ssbe = (SuitStationBlockEntity)be;
				int tick = world.getServer().getTicks();
				if (ssbe.lastCollisionTick != tick) {
					if (Yttr.isWearingFullSuit(le)) {
						ssbe.lastCollisionTick = tick;
						ItemStack chest = le.getEquippedStack(EquipmentSlot.CHEST);
						SuitArmorItem sai = (SuitArmorItem)chest.getItem();
						sai.replenishResource(chest, SuitResource.OXYGEN, 100);
						sai.replenishResource(chest, SuitResource.INTEGRITY, 2);
					}
				}
			}
		}
	}
	
}

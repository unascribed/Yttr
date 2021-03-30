package com.unascribed.yttr;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BedrockSmasherBlock extends Block implements Shootable {
	private static final VoxelShape SHAPE = VoxelShapes.union(
			VoxelShapes.cuboid(6/16D, 0, 2/16D, 10/16D, 13/16D, 14/16D),
			VoxelShapes.cuboid(0, 12.5/16D, 0, 1, 13.5/16D, 1)
		);

	public BedrockSmasherBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public boolean onShotByRifle(World world, BlockState bs, LivingEntity user, RifleMode mode, float power, BlockPos pos, BlockHitResult bhr) {
		if (power > 1.1f && world.getRegistryKey().getValue().toString().equals("minecraft:overworld")
				&& pos.getY() < 10 && bhr.getSide() == Direction.UP) {
			BlockPos down = pos.down();
			if (world.getBlockState(down).isOf(Blocks.BEDROCK)) {
				if (down.getY() == 0) {
					world.setBlockState(down, Yttr.VOID_GEYSER.getDefaultState());
					world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 1, 0.5f);
				} else {
					world.setBlockState(down, Yttr.RUINED_BEDROCK.getDefaultState());
					world.breakBlock(down.north(), true, user);
					world.breakBlock(down.south(), true, user);
				}
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 1, 2);
				world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 1, 1.5f);
				world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS, 1, 0.5f);
				if (world instanceof ServerWorld) {
					((ServerWorld)world).spawnParticles(ParticleTypes.EXPLOSION, down.getX()+0.5, down.getY()+1, down.getZ()+0.5, 8, 1, 1, 1, 0);
				}
			}
		}
		return false;
	}
	
	
}
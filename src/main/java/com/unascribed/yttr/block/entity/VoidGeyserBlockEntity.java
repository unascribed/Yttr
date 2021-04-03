package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YSounds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class VoidGeyserBlockEntity extends BlockEntity implements Tickable {

	public int age;
	
	public VoidGeyserBlockEntity() {
		super(YBlockEntities.VOID_GEYSER);
	}

	@Override
	public void tick() {
		age++;
		if (world.isClient) return;
		if (pos.getY() != 0 && !world.getBlockState(pos.down()).isOf(Blocks.BEDROCK)) {
			world.createExplosion(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 4, DestructionType.NONE);
			world.setBlockState(pos, Blocks.VOID_AIR.getDefaultState());
			return;
		}
		int ticksPerUpdate = 2;
		int gushHeight = 7;
		int floodHeight = 3;
		int floodRadius = 3;
		int restTime = 40;
		
		if (age%ticksPerUpdate != 0) return;
		
		int floodDiameter = (floodRadius*2)+1;
		int floodArea = floodDiameter*floodDiameter;
		int gushArea = (gushHeight*5)+1;
		int i = (age/ticksPerUpdate)%(gushArea+(floodHeight*floodArea)+restTime);
		if (i < gushArea) {
			int j = i%5;
			BlockPos cur = pos.up((i/5)+1);
			if (j > 0) {
				cur = cur.offset(Direction.fromHorizontal(j-1));
			}
			destroyIfAble(cur);
		} else if (i < gushArea+(floodHeight*floodArea)) {
			int j = i-gushArea;
			int y = j/floodArea;
			int x = 0;
			int z = -1;
			
			Direction dir = Direction.WEST;
			int legLength = 0;
			int legIdx = 0;
			int legCount = 0;
			
			for (int k = 0; k <= j%floodArea; k++) {
				if (legIdx >= legLength) {
					dir = dir.rotateYCounterclockwise();
					legIdx = 0;
					legCount++;
					if (legCount%2 == 0) legLength++;
				}
				x += dir.getOffsetX();
				z += dir.getOffsetZ();
				legIdx++;
			}
			destroyIfAble(pos.add(x, y+1, z));
		}
	}

	private void destroyIfAble(BlockPos pos) {
		Block block = YBlocks.VOID;
//		Block block = Blocks.GRAY_WOOL;
		BlockState bs = world.getBlockState(pos);
		if (!bs.isOf(block) && (bs.isAir() || bs.getHardness(world, pos) >= 0)) {
			if (bs.getFluidState().isIn(FluidTags.LAVA)) {
				world.setBlockState(pos, Blocks.BEDROCK.getDefaultState());
			} else {
				if (!bs.isAir()) {
					if (world instanceof ServerWorld) {
						((ServerWorld)world).spawnParticles(ParticleTypes.LARGE_SMOKE, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 20, 0.2, 0.2, 0.2, 0);
					}
					world.playSound(null, pos, YSounds.DISSOLVE, SoundCategory.BLOCKS, 1, 0.8f+(world.random.nextFloat()*0.4f));
				}
				world.setBlockState(pos, block.getDefaultState());
				world.getFluidTickScheduler().schedule(pos, YFluids.VOID, 1);
			}
		}
	}

}

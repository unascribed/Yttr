package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.content.block.void_.VoidGeyserBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.mechanics.rifle.RifleMode;
import com.unascribed.yttr.mechanics.rifle.Shootable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class EncasedVoidFilterBlock extends Block implements Shootable {

	public EncasedVoidFilterBlock(Settings settings) {
		super(settings);
	}

	@Override
	public boolean onShotByRifle(World world, BlockState bs, LivingEntity user, RifleMode mode, float power, BlockPos pos, BlockHitResult bhr) {
		if (mode == RifleMode.EXPLODE && power > 1.1f && world.getRegistryKey().getValue().toString().equals("minecraft:overworld") && pos.getY() == 2 && bhr.getSide() == Direction.UP) {
			BlockPos down = pos.down();
			BlockPos downDown = pos.down().down();
			if (world.getBlockState(down).isOf(YBlocks.BEDROCK_SMASHER) && world.getBlockState(downDown).isOf(Blocks.BEDROCK)) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				world.setBlockState(down, YBlocks.VOID_FILTER.getDefaultState());
				world.setBlockState(downDown, YBlocks.VOID_GEYSER.getDefaultState());
				VoidGeyserBlockEntity.setDefaultName(world, downDown, user);
				world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, YSounds.SNAP, SoundCategory.BLOCKS, 1, 2);
				world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, YSounds.SNAP, SoundCategory.BLOCKS, 1, 1.5f);
				world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, YSounds.CLANG, SoundCategory.BLOCKS, 1, 0.5f);
				if (world instanceof ServerWorld) {
					((ServerWorld)world).spawnParticles(ParticleTypes.EXPLOSION, down.getX()+0.5, down.getY()+1, down.getZ()+0.5, 8, 1, 1, 1, 0);
				}
				YStats.add(user, YStats.FILTERS_INSTALLED, 1);
				user.world.createExplosion(null, DamageSource.explosion(user), null, bhr.getPos().x, bhr.getPos().y, bhr.getPos().z, 3.3f, false, DestructionType.NONE);
				return true;
			}
		}
		return false;
	}
	
	
}
package com.unascribed.yttr.block;

import java.util.Random;

import com.unascribed.yttr.SolventDamageSource;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot.Type;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class VoidFluidBlock extends FluidBlock {
	private static final DustParticleEffect BLACK_DUST = new DustParticleEffect(0, 0, 0, 1);
	
	public VoidFluidBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.isAir(pos.up())) {
			for (int i = 0; i < 4; i++) {
				world.addParticle(BLACK_DUST, pos.getX()+random.nextDouble(), pos.getY()+0.5, pos.getZ()+random.nextDouble(),
						0, 1, 0);
			}
		}
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!entity.isAlive()) return;
		if (Yttr.isWearingFullSuit(entity)) return;
		int i = 1;
		if (entity instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)entity;
			DamageSource recent = le.getRecentDamageSource();
			if (recent instanceof SolventDamageSource) {
				i = ((SolventDamageSource) recent).i+1;
				if (le.timeUntilRegen > 10) return;
			}
		}
		if (entity instanceof ItemEntity || entity.damage(new SolventDamageSource(i), 2*i)) {
			if (entity instanceof ItemEntity) {
				ItemStack stack = ((ItemEntity) entity).getStack();
				if (stack.getItem().isIn(YTags.Item.VOID_IMMUNE)) return;
				if (stack.getItem() == YItems.BEDROCK_SHARD) {
					if (getFluidState(state).isStill()) {
						world.setBlockState(pos, YBlocks.GLASSY_VOID.getDefaultState());
					} else {
						return;
					}
				}
				entity.remove();
			}
			if (entity instanceof LivingEntity) {
				LivingEntity le = (LivingEntity)entity;
				for (EquipmentSlot es : EquipmentSlot.values()) {
					if (es.getType() == Type.ARMOR) {
						le.getEquippedStack(es).damage(3*i, le, (e) -> {
							le.sendEquipmentBreakStatus(es);
						});
					}
				}
			}
			world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, YSounds.DISSOLVE, SoundCategory.BLOCKS, 1, 0.8f+(i*0.1f));
			if (world instanceof ServerWorld) {
				Box b = entity.getBoundingBox();
				((ServerWorld)world).spawnParticles(BLACK_DUST, b.getCenter().x, b.getCenter().y, b.getCenter().z, 30, b.getXLength()/2, b.getYLength()/2, b.getZLength()/2, 0.5);
			}
		}
	};
}

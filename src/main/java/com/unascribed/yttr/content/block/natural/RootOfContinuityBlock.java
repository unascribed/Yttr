package com.unascribed.yttr.content.block.natural;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixin.accessor.AccessorEndermanEntity;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class RootOfContinuityBlock extends Block {

	public static final BooleanProperty ANCHOR = BooleanProperty.of("anchor");
	
	public static final BlockSoundGroup SOUND_GROUP = new BlockSoundGroup(1, 1, YSounds.ROOTBREAK, YSounds.ROOTSTEP, YSounds.ROOTSTEP, YSounds.ROOTHIT, YSounds.ROOTSTEP);
	public static final BlockSoundGroup SOUND_GROUP_INEFFECTIVE = new BlockSoundGroup(1, 1, YSounds.ROOTBREAK, YSounds.ROOTSTEP, YSounds.ROOTSTEP, YSounds.ROOTSTEP, YSounds.ROOTSTEP);
	
	public static int maxPiercingLevel_MutableOnPurposeHiKat = 10;
	
	private final ThreadLocal<BlockSoundGroup> currentGroup = ThreadLocal.withInitial(() -> SOUND_GROUP);
	
	public RootOfContinuityBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(ANCHOR, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(ANCHOR);
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		if (player.isUsingEffectiveTool(state)) {
			currentGroup.set(SOUND_GROUP);
			if (world instanceof ServerWorld) {
				((ServerWorld)world).spawnParticles(ParticleTypes.FIREWORK, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 3, 0.3, 0.3, 0.3, 0);
			}
			int lvl = EnchantmentHelper.getLevel(Enchantments.PIERCING, player.getStackInHand(Hand.MAIN_HAND));
			return 0.01f*((lvl*2)+1);
		}
		currentGroup.set(SOUND_GROUP_INEFFECTIVE);
		return 0;
	}
	
	@Override
	public BlockSoundGroup getSoundGroup(BlockState state) {
		return currentGroup.get();
	}
	
	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
		if (world instanceof ServerWorld && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
			player.incrementStat(Stats.MINED.getOrCreateStat(this));
			player.addExhaustion(0.5f);
			getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, player, stack).forEach((itemStack) -> {
				if (!world.isClient && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
					double x = pos.getX() + (world.random.nextFloat() * 0.75 + 0.25);
					double y = pos.getY() + (world.random.nextFloat() * 0.75 + 0.25);
					double z = pos.getZ() + (world.random.nextFloat() * 0.75 + 0.25);
					ItemEntity ie = new ItemEntity(world, x, y, z, itemStack);
					ie.setVelocity(player.getPos().subtract(x, y, z).normalize().multiply(0.2));
					ie.setToDefaultPickupDelay();
					world.spawnEntity(ie);
				}
			});
			state.onStacksDropped((ServerWorld)world, pos, stack);
		} else {
			super.afterBreak(world, player, pos, state, blockEntity, stack);
		}
		if (!world.isClient && state.get(ANCHOR)) {
			if (world.random.nextInt(10) == 0 && player.isUsingEffectiveTool(state)) {
				ItemStack held = player.getStackInHand(Hand.MAIN_HAND);
				Map<Enchantment, Integer> ench = EnchantmentHelper.get(held);
				if (!ench.isEmpty()) {
					ench.compute(Enchantments.PIERCING, (k, v) -> Math.min(maxPiercingLevel_MutableOnPurposeHiKat, (v == null ? 0 : v) + 1));
					EnchantmentHelper.set(ench, held);
					player.setStackInHand(Hand.MAIN_HAND, held);
					if (world instanceof ServerWorld) {
						((ServerWorld)world).spawnParticles(ParticleTypes.ENCHANT, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 3, 0.3, 0.3, 0.3, 0);
					}
				}
			}
			Set<BlockPos> seen = Sets.newHashSet();
			Set<BlockPos> scan = Sets.newHashSet();
			Set<BlockPos> nextScan = Sets.newHashSet();
			int i = 0;
			glass: for (int y = 0; y < 32; y++) {
				scan.clear();
				nextScan.clear();
				scan.add(pos.up(y));
				while (!scan.isEmpty()) {
					if (i++ > 384) break glass;
					for (BlockPos bp : scan) {
						BlockState bs = world.getBlockState(bp);
						if (bs.isOf(Blocks.END_STONE) || bp.equals(pos)) {
							for (Direction d : Direction.Type.HORIZONTAL) {
								BlockPos c = bp.offset(d);
								if (seen.add(c)) {
									nextScan.add(c);
								}
							}
						}
						if (bs.isOf(Blocks.END_STONE)) {
							FallingBlockEntity fbe = new FallingBlockEntity(world, bp.getX()+0.5, bp.getY(), bp.getZ()+0.5, bs);
							world.spawnEntity(fbe);
						}
					}
					scan.clear();
					scan.addAll(nextScan);
					nextScan.clear();
				}
			}
			boolean foundAny = false;
			int teleported = 0;
			for (EndermanEntity ent : world.getEntitiesByClass(EndermanEntity.class, player.getBoundingBox().expand(64), e -> true)) {
				ent.setTarget(player);
				if (world.random.nextInt(5*(teleported+1)) == 0) {
					Vec3d playerPos = player.getPos();
					((AccessorEndermanEntity)ent).yttr$teleportTo(playerPos.x, playerPos.y, playerPos.z);
				}
				foundAny = true;
			}
			if (foundAny) {
				world.playSound(null, player.getPos().x, player.getPos().y, player.getPos().z, SoundEvents.ENTITY_ENDERMAN_STARE, SoundCategory.HOSTILE, 1, 1);
			}
		}
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);
		boolean anchor = state.get(ANCHOR);
		for (int i = 0; i < 6; i++) {
			world.addParticle(ParticleTypes.CRIT, pos.getX()+random.nextDouble(), pos.getY()+random.nextDouble(), pos.getZ()+random.nextDouble(), (random.nextDouble()-0.5)/2, (random.nextDouble()-0.5)/2, (random.nextDouble()-0.5)/2);
			if (anchor) {
				world.addParticle(ParticleTypes.FIREWORK, pos.getX()+0.5, pos.getY()-0.05, pos.getZ()+0.5, (random.nextDouble()-0.5)/4, (random.nextDouble()-0.5)/4, (random.nextDouble()-0.5)/4);
			}
		}
	}

}

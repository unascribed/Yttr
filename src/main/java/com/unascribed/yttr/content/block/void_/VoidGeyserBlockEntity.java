package com.unascribed.yttr.content.block.void_;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.util.math.Vec2i;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class VoidGeyserBlockEntity extends BlockEntity implements Tickable {

	public int age;
	
	private UUID id = UUID.randomUUID();
	private String name = "Unnamed";
	
	private final Map<UUID, MutableInt> sneakTimers = Maps.newHashMap();
	private final Set<UUID> seen = Sets.newHashSet();
	
	public VoidGeyserBlockEntity() {
		super(YBlockEntities.VOID_GEYSER);
	}

	@Override
	public void tick() {
		if (pos.getY() != 0 && !world.getBlockState(pos.down()).isOf(Blocks.BEDROCK)) {
			world.createExplosion(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 4, DestructionType.NONE);
			world.setBlockState(pos, Blocks.VOID_AIR.getDefaultState());
			return;
		}
		if (world.getBlockState(pos.up()).isOf(YBlocks.VOID_FILTER)) return;
		
		age++;
		
		if (world.isClient) return;
		
		if (world instanceof ServerWorld) {
			GeysersState gs = GeysersState.get((ServerWorld)world);
			Geyser g = gs.getGeyser(id);
			if (g == null) {
				g = new Geyser(id, pos, name);
				gs.addGeyser(g);
			}
		}
		
		seen.clear();
		for (ServerPlayerEntity p : world.getEntitiesByClass(ServerPlayerEntity.class, new Box(pos).expand(5), e -> e instanceof DiverPlayer)) {
			Yttr.discoverGeyser(id, p);
			if (Yttr.isWearingFullSuit(p) && p.isSneaking() && Yttr.isStandingOnDivingPlate(p)) {
				if (sneakTimers.compute(p.getUuid(), (u, mi) -> {
					if (mi != null) {
						mi.increment();
						return mi;
					}
					return new MutableInt(0);
				}).intValue() > 40) {
					p.playSound(YSounds.DIVE_MONO, 2, 1);
					DiverPlayer diver = (DiverPlayer)p;
					diver.yttr$setDiving(true);
					diver.yttr$setDivePos(new Vec2i(pos.getX(), pos.getZ()));
					p.teleport(pos.getX()+0.5, -12, pos.getZ()+0.5);
					p.setVelocity(0, 0, 0);
					Yttr.syncDive(p);
				} else {
					seen.add(p.getUuid());
				}
			}
		}
		sneakTimers.keySet().retainAll(seen);
		
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
		if (!bs.isOf(block) && !bs.isOf(YBlocks.DIVING_PLATE) && (bs.isAir() || bs.getHardness(world, pos) >= 0)) {
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
	
	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		if (world instanceof ServerWorld) {
			GeysersState gs = GeysersState.get((ServerWorld)world);
			Geyser g = gs.getGeyser(id);
			if (g != null) {
				g.name = name;
			}
		}
		markDirty();
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		if (world instanceof ServerWorld) {
			GeysersState.get((ServerWorld)world).markDirty();
		}
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putUuid("ID", id);
		tag.putString("Name", name);
		return super.toTag(tag);
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		id = tag.containsUuid("ID") ? tag.getUuid("ID") : UUID.randomUUID();
		name = tag.getString("Name");
	}

	public static void setDefaultName(World world, BlockPos pos, @Nullable LivingEntity creator) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof VoidGeyserBlockEntity) {
			Biome b = world.getBiome(pos);
			Identifier biomeId = world.getRegistryManager().get(Registry.BIOME_KEY).getId(b);
			((VoidGeyserBlockEntity)be).setName((creator == null ? "" : creator.getName().getString()+"'s ")+Language.getInstance().get("biome."+biomeId.getNamespace()+"."+biomeId.getPath()));
		}
	}

}

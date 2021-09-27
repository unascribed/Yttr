package com.unascribed.yttr.content.block;

import java.util.Random;

import com.google.common.base.Ascii;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class ContinuousPlatformBlock extends Block implements BlockColorProvider {
	
	public enum Age implements StringIdentifiable {
		IMMORTAL,
		_0,
		_1,
		_2,
		_3
		;
		private final String name;
		Age() {
			if (name().charAt(0) == '_') {
				name = name().substring(1);
			} else {
				name = Ascii.toLowerCase(name());
			}
		}
		@Override
		public String asString() {
			return name;
		}
	}
	
	public static final EnumProperty<Age> AGE = EnumProperty.of("age", Age.class);
	
	public ContinuousPlatformBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(AGE, Age._0));
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom == state;
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		float h1 = (((pos.getX()+pos.getY()+pos.getZ()))/20f)%1;
		if (h1 < 0) h1 += 1;
		return MathHelper.hsvToRgb(h1, 0.3f, 1f);
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(AGE) == Age.IMMORTAL) return;
		if (state.get(AGE) == Age._3) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		} else {
			world.setBlockState(pos, state.cycle(AGE));
		}
	}
	
	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		if (world.getBlockState(pos).get(AGE) != Age.IMMORTAL) {
			world.setBlockState(pos, world.getBlockState(pos).with(AGE, Age._0));
		}
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return 0;
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		BlockPos down = pos.down();
		if (!world.getBlockState(down).isSolidBlock(world, down)) {
			world.addParticle(ParticleTypes.FIREWORK, pos.getX()+random.nextDouble(), pos.getY(), pos.getZ()+random.nextDouble(), 0, -0.05, 0);
		}
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (state.get(AGE) != Age.IMMORTAL) {
			world.getBlockTickScheduler().schedule(pos, this, 150+world.random.nextInt(20));
		}
	}
	
}

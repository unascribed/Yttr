package com.unascribed.yttr.block.natural;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.unascribed.yttr.client.particle.DrippingDelicaceParticle;
import com.unascribed.yttr.init.YStatusEffects;

import com.google.common.collect.ImmutableList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DelicaceBlock extends Block {

	public static final IntProperty STAGE = IntProperty.of("stage", 0, 7);
	
	public DelicaceBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(STAGE);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES.get(state.get(STAGE)/2);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (getOutlineShape(state, world, pos, ShapeContext.absent()).getBoundingBox().offset(pos).intersects(entity.getBoundingBox())) {
			entity.slowMovement(state, new Vec3d(0.8, 0.75, 0.8));
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int stage = state.get(STAGE);
		if (stage < 7 && random.nextInt(8) == 0) {
			world.setBlockState(pos, state.with(STAGE, stage+1));
		}
		if (stage > 2) {
			Box box = new Box(pos).expand(8);
			if (!world.getEntitiesByClass(PlayerEntity.class, box, e -> true).isEmpty()) {
				world.getBlockTickScheduler().schedule(pos, this, 1);
			}
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		Box box = new Box(pos).expand(8);
		List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, box, e -> true);
		if (!players.isEmpty()) {
			int potency = state.get(STAGE) == 7 ? 1 : 0;
			for (PlayerEntity pe : players) {
				pe.addStatusEffect(new StatusEffectInstance(YStatusEffects.DELICACENESS, (20*15)+19, potency));
			}
			world.getBlockTickScheduler().schedule(pos, this, 18);
		}
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getDefaultState();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double yOfs;
		if (state.isOf(this)) {
			yOfs = (getOutlineShape(state, world, pos, ShapeContext.absent()).getMin(Axis.Y));
		} else {
			yOfs = 0;
		}
		DrippingDelicaceParticle p = new DrippingDelicaceParticle(MinecraftClient.getInstance().world,
				pos.getX()+random.nextFloat(), pos.getY()+yOfs, pos.getZ()+random.nextFloat());
		MinecraftClient.getInstance().particleManager.addParticle(p);
	}
	
	private static final ImmutableList<VoxelShape> SHAPES = ImmutableList.of(
			Stream.of(
					createInvCuboidShape(5, 0, 4, 11, 1, 5),
					createInvCuboidShape(5, 0, 11, 11, 1, 12),
					createInvCuboidShape(4, 0, 5, 5, 1, 11),
					createInvCuboidShape(11, 0, 5, 12, 1, 11),
					createInvCuboidShape(5, 0, 5, 11, 1, 11)
				).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
			Stream.of(
					createInvCuboidShape(5, 0, 4, 11, 1, 5),
					createInvCuboidShape(5, 0, 11, 11, 1, 12),
					createInvCuboidShape(4, 0, 5, 5, 1, 11),
					createInvCuboidShape(11, 0, 5, 12, 1, 11),
					createInvCuboidShape(5, 1, 5, 11, 2, 11)
				).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
			Stream.of(
					createInvCuboidShape(5, 0, 3, 11, 1, 4),
					createInvCuboidShape(5, 0, 4, 11, 1, 5),
					createInvCuboidShape(5, 0, 12, 11, 1, 13),
					createInvCuboidShape(5, 0, 11, 11, 1, 12),
					createInvCuboidShape(3, 0, 5, 4, 1, 11),
					createInvCuboidShape(4, 0, 4, 5, 1, 12),
					createInvCuboidShape(12, 0, 5, 13, 1, 11),
					createInvCuboidShape(11, 0, 4, 12, 1, 12),
					createInvCuboidShape(5, 1, 5, 11, 2, 11)
				).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
			Stream.of(
					createInvCuboidShape(5, 0, 2, 11, 1, 4),
					createInvCuboidShape(5, 0, 4, 6, 1, 5),
					createInvCuboidShape(10, 0, 4, 11, 1, 5),
					createInvCuboidShape(5, 0, 12, 11, 1, 14),
					createInvCuboidShape(5, 0, 11, 6, 1, 12),
					createInvCuboidShape(9, 0, 11, 11, 1, 12),
					createInvCuboidShape(2, 0, 5, 3, 1, 11),
					createInvCuboidShape(3, 0, 4, 4, 1, 12),
					createInvCuboidShape(4, 0, 10, 5, 1, 13),
					createInvCuboidShape(4, 0, 3, 5, 1, 6),
					createInvCuboidShape(13, 0, 5, 14, 1, 11),
					createInvCuboidShape(12, 0, 4, 13, 1, 12),
					createInvCuboidShape(11, 0, 3, 12, 1, 6),
					createInvCuboidShape(11, 0, 10, 12, 1, 13),
					createInvCuboidShape(5, 1, 5, 11, 2, 11),
					createInvCuboidShape(5, 1, 10, 6, 2, 11),
					createInvCuboidShape(3, 0, 11, 4, 1, 12),
					createInvCuboidShape(5, 0, 13, 6, 1, 14),
					createInvCuboidShape(5, 0, 2, 6, 1, 3),
					createInvCuboidShape(10, 0, 2, 11, 1, 3),
					createInvCuboidShape(12, 0, 4, 13, 1, 5),
					createInvCuboidShape(12, 0, 11, 13, 1, 12),
					createInvCuboidShape(10, 0, 13, 11, 1, 14),
					createInvCuboidShape(3, 0, 4, 4, 1, 5),
					createInvCuboidShape(10, 1, 10, 11, 2, 11),
					createInvCuboidShape(10, 1, 5, 11, 2, 6),
					createInvCuboidShape(5, 1, 5, 6, 2, 6),
					createInvCuboidShape(6, 1, 11, 10, 2, 12),
					createInvCuboidShape(4, 1, 6, 5, 2, 10),
					createInvCuboidShape(11, 1, 6, 12, 2, 10),
					createInvCuboidShape(6, 1, 4, 10, 2, 5)
				).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get()
	);

	private static VoxelShape createInvCuboidShape(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		return createCuboidShape(xMin, 16-yMin, zMin, xMax, 16-yMax, zMax);
	}
	
}

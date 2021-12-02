package com.unascribed.yttr.content.block.decor;

import java.util.Collections;
import java.util.List;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixinsupport.SlopeStander;
import com.unascribed.yttr.util.math.partitioner.DEdge;
import com.unascribed.yttr.util.math.partitioner.Polygon;
import com.unascribed.yttr.util.math.partitioner.Where;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class CleavedBlock extends Block implements BlockEntityProvider, BlockColorProvider, Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);
	
	public CleavedBlock(Settings settings) {
		super(settings.luminance(bs -> bs.get(LUMINANCE)));
		setDefaultState(getDefaultState().with(WATERLOGGED, false).with(LUMINANCE, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, LUMINANCE);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new CleavedBlockEntity();
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getDefaultState() : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity) be).getShape();
		}
		return super.getOutlineShape(state, world, pos, context);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
		BlockEntity be = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().getDroppedStacks(builder);
		}
		return Collections.emptyList();
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			world.syncWorldEvent(player, 2001, pos, getRawIdFromState(((CleavedBlockEntity) be).getDonor()));
		}
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().calcBlockBreakingDelta(player, world, pos);
		}
		return super.calcBlockBreakingDelta(state, player, world, pos);
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		// lie, for grass
		return 0;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().getBlock().getPickStack(world, pos, ((CleavedBlockEntity)be).getDonor());
		}
		return super.getPickStack(world, pos, state);
	}
	
	public void onEntityNearby(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			// we don't check onGround as then you "unsnap" while going down a slope and it looks really bad
			if (entity instanceof FlyingEntity || (entity instanceof PlayerEntity && (((PlayerEntity)entity).isFallFlying() || ((PlayerEntity)entity).abilities.flying))) {
				return;
			}
			List<Polygon> shape = ((CleavedBlockEntity)be).getPolygons();
			double checkDist = 2D/CleavedBlockEntity.SHAPE_GRANULARITY;
			Box box = entity.getBoundingBox();
			double x = box.getCenter().x;
			double y = box.minY;
			double z = box.getCenter().z;
			int signum = -1;
			if (box.minY < pos.getY()) {
//				y = box.maxY;
//				signum = 1;
				return;
			}
			Polygon relevant = null;
			for (Polygon p : shape) {
				Vec3d normal = p.plane().normal();
				if ((normal.x == 0 && normal.y == 0) ||
						(normal.x == 0 && normal.z == 0) ||
						(normal.z == 0 && normal.y == 0)) {
					// flat face, does not need adjustment
					continue;
				}
				relevant = p;
				break;
			}
			x -= pos.getX();
			y -= pos.getY();
			z -= pos.getZ();
			if (relevant == null) return;
			double pMinY = 200;
			double pMaxY = -200;
			for (DEdge edge : relevant) {
				pMinY = Math.min(pMinY, edge.srcPoint().y);
				pMaxY = Math.max(pMaxY, edge.srcPoint().y);
			}
			if (y > pMaxY+0.05 || y < pMinY-0.05) return;
			double origY = y;
			y -= (checkDist*signum);
			int steps = 64;
			boolean found = false;
			Vec3d up = new Vec3d(0, 1, 0);
			float steepness = 0;
			double dist = checkDist*4;
			for (int i = 0; i < steps; i++) {
				y += (dist/steps)*signum;
				Vec3d vec = new Vec3d(x, y, z);
				if (relevant.plane().whichSide(vec) != Where.ABOVE) {
					found = true;
					steepness = (float)(1-relevant.plane().normal().dotProduct(up));
					break;
				}
			}
			if (found) {
				SlopeStander ss = ((SlopeStander)entity);
				BlockPos below = entity.getBlockPos().down();
				BlockState belowState = world.getBlockState(below);
				if (!belowState.isOf(YBlocks.CLEAVED_BLOCK)) {
					VoxelShape belowShape = belowState.getCollisionShape(world, below);
					Vec3d point = new Vec3d(x+pos.getX(), y+pos.getY(), z+pos.getZ());
					for (Box b : belowShape.getBoundingBoxes()) {
						if (b.offset(below).contains(point)) {
							// colliding with a non-cleaved block; avoid causing the player to seem to clip into normal blocks
							return;
						}
					}
				}
				if (YConfig.Client.slopeSmoothing) {
					double yO = y-origY;
					if (ss.yttr$getYOffset() == 0 || ss.yttr$getYOffset() < yO) {
						ss.yttr$setYOffset(yO);
					}
				}
				ss.yttr$setSlopeSteepness(Math.max(ss.yttr$getSlopeSteepness(), steepness));
			}
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return MinecraftClient.getInstance().getBlockColors().getColor(((CleavedBlockEntity)be).getDonor(), world, pos, tintIndex);
		}
		return -1;
	}
	
}

package com.unascribed.yttr.content.block;

import java.util.List;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mechanics.SimpleLootBlock;
import com.unascribed.yttr.mixin.accessor.AccessorVoxelShape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes.BoxConsumer;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NeodymiumBlock extends SlabBlock implements SimpleLootBlock {

	public NeodymiumBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		if (ctx.getStack().getItem() == YItems.NEODYMIUM_BLOCK)
			return getDefaultState().with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		return super.getPlacementState(ctx);
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getLoot(state);
	}

	@Override
	public ItemStack getLoot(BlockState state) {
		return new ItemStack(state.get(TYPE) == SlabType.DOUBLE ? YItems.NEODYMIUM_BLOCK : YItems.NEODYMIUM_SLAB);
	}
	
	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
		list.add(new ItemStack(YItems.NEODYMIUM_BLOCK));
		list.add(new ItemStack(YItems.NEODYMIUM_SLAB));
	}
	
	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity pe = (PlayerEntity)entity;
			Yttr.trinketsAccess.dropMagneticTrinkets(pe);
		}
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return new MagneticVoxelShape(super.getCollisionShape(state, world, pos, context));
	}
	
	public static class MagneticVoxelShape extends VoxelShape {

		private final VoxelShape delegate;
		
		public MagneticVoxelShape(VoxelShape delegate) {
			super(((AccessorVoxelShape)delegate).yttr$getVoxels());
			this.delegate = delegate;
		}

		@Override
		public double getMin(Axis axis) {
			return delegate.getMin(axis);
		}

		@Override
		public double getMax(Axis axis) {
			return delegate.getMax(axis);
		}

		@Override
		public Box getBoundingBox() {
			return delegate.getBoundingBox();
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public VoxelShape offset(double x, double y, double z) {
			return new MagneticVoxelShape(delegate.offset(x, y, z));
		}

		@Override
		public VoxelShape simplify() {
			return new MagneticVoxelShape(delegate.simplify());
		}

		@Override
		public void forEachEdge(BoxConsumer boxConsumer) {
			delegate.forEachEdge(boxConsumer);
		}

		@Override
		public void forEachBox(BoxConsumer boxConsumer) {
			delegate.forEachBox(boxConsumer);
		}

		@Override
		public List<Box> getBoundingBoxes() {
			return delegate.getBoundingBoxes();
		}

		@Override
		public double getEndingCoord(Axis axis, double from, double to) {
			return delegate.getEndingCoord(axis, from, to);
		}

		@Override
		public BlockHitResult raycast(Vec3d start, Vec3d end, BlockPos pos) {
			return delegate.raycast(start, end, pos);
		}

		@Override
		public VoxelShape getFace(Direction facing) {
			return new MagneticVoxelShape(delegate.getFace(facing));
		}

		@Override
		public double calculateMaxDistance(Axis axis, Box box, double maxDist) {
			return delegate.calculateMaxDistance(axis, box, maxDist);
		}

		@Override
		public String toString() {
			return "Magnetic"+delegate.toString();
		}

		@Override
		protected double getPointPosition(Axis axis, int index) {
			return ((AccessorVoxelShape)delegate).yttr$getPointPosition(axis, index);
		}

		@Override
		protected DoubleList getPointPositions(Axis axis) {
			return ((AccessorVoxelShape)delegate).yttr$getPointPositions(axis);
		}

		@Override
		protected int getCoordIndex(Axis axis, double coord) {
			return ((AccessorVoxelShape)delegate).yttr$getCoordIndex(axis, coord);
		}

		@Override
		protected boolean contains(double x, double y, double z) {
			return ((AccessorVoxelShape)delegate).yttr$contains(x, y, z);
		}

		@Override
		protected double calculateMaxDistance(AxisCycleDirection axisCycle, Box box, double maxDist) {
			return ((AccessorVoxelShape)delegate).yttr$calculateMaxDistance(axisCycle, box, maxDist);
		}

	}
	
}

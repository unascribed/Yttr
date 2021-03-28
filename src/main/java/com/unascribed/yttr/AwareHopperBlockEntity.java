package com.unascribed.yttr;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class AwareHopperBlockEntity extends BlockEntity implements Tickable {

	public float zendermieYaw;
	public float prevZendermieYaw;
	public float zendermiePitch;
	public float prevZendermiePitch;
	
	public int age;
	
	private int sayTicks = -60;
	
	public AwareHopperBlockEntity() {
		super(Yttr.AWARE_HOPPER_ENTITY);
	}

	@Override
	public void tick() {
		age++;
		boolean blind = isBlind();
		this.prevZendermieYaw = zendermieYaw;
		this.prevZendermiePitch = zendermiePitch;
		if (!blind) {
			Vec3d head = getHeadPos();
			PlayerEntity player = world.getClosestPlayer(head.x, head.y, head.z, 4, false);
			if (player != null) {
				Vec3d delta = player.getCameraPosVec(1).subtract(head);
				this.zendermieYaw = (float) Math.toDegrees(MathHelper.atan2(delta.z, delta.x));
				this.zendermiePitch = (float) Math.toDegrees(MathHelper.atan2(-delta.y, Math.sqrt(delta.x*delta.x + delta.z*delta.z)));
				markDirty();
			} else {
				if (zendermiePitch > 40) {
					zendermiePitch = Math.max(zendermiePitch-5, 40);
					markDirty();
				} else if (zendermiePitch < 40) {
					zendermiePitch = Math.min(zendermiePitch+5, 40);
					markDirty();
				}
			}
		}
		if (!world.isClient) {
			if (world.random.nextInt(1000) < sayTicks++) {
				sayTicks = -60;
				world.playSound(null, pos, Yttr.AWARE_HOPPER_AMBIENT, SoundCategory.BLOCKS, blind ? 0.3f : 0.7f, (world.random.nextFloat()-world.random.nextFloat())*0.2f + 1);
			}
		} else if (world.random.nextInt(5) == 0) {
			world.addParticle(ParticleTypes.PORTAL,
					pos.getX() + 0.5, pos.getY() + 1.75, pos.getZ() + 0.5, (world.random.nextDouble() - 0.5) * 2, -world.random.nextDouble(), (world.random.nextDouble() - 0.5) * 2);
		}
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.putFloat("Yaw", zendermieYaw);
		tag.putFloat("Pitch", zendermiePitch);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		zendermieYaw = prevZendermieYaw = tag.getFloat("Yaw");
		zendermiePitch = prevZendermiePitch = tag.getFloat("Pitch");
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag tag = super.toInitialChunkDataTag();
		tag.putFloat("Yaw", zendermieYaw);
		tag.putFloat("Pitch", zendermiePitch);
		return tag;
	}

	public boolean isBlind() {
		BlockState bs = world.getBlockState(pos);
		return bs.getBlock() == Yttr.AWARE_HOPPER && bs.get(AwareHopperBlock.BLIND);
	}

	public void onNearbyCrafting(PlayerEntity player, CraftingInventory input) {
		if (!isBlind()) {
			if (player.squaredDistanceTo(getHeadPos()) < 4*4) {
				if (player.world.raycast(new RaycastContext(getHeadPos(), player.getCameraPosVec(1), ShapeType.COLLIDER, FluidHandling.NONE, player)).getType() == Type.MISS) {
					System.out.println("Time to learn");
				}
			}
		}
	}

	public Vec3d getHeadPos() {
		return new Vec3d(pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5);
	}
	
}

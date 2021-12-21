package com.unascribed.yttr.content.item;

import com.unascribed.yttr.DelayedTask;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.ContinuousPlatformBlock;
import com.unascribed.yttr.content.block.ContinuousPlatformBlock.Age;
import com.unascribed.yttr.content.block.ContinuousPlatformBlock.LogFluid;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YSounds;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

public class ProjectorItem extends Item {

	public ProjectorItem(Settings settings) {
		super(settings);
		DispenserBlock.registerBehavior(this, (ptr, stack) -> {
			World w = ptr.getWorld();
			BlockPos.Mutable mut = ptr.getBlockPos().mutableCopy();
			Direction face = ptr.getBlockState().get(DispenserBlock.FACING);
			mut.move(face, face.getAxis() == Axis.Y ? 1 : 2);
			int i = 0;
			while (canReplace(w.getBlockState(mut))) {
				if (i > 64) break;
				BlockPos pos = mut.toImmutable();
				Yttr.delayedServerTasks.add(new DelayedTask(i*2, () -> {
					createPlatform(w, pos);
				}));
				mut.move(face);
				i++;
			}
			return stack;
		});
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (stack.hasTag()) stack.getTag().remove("LastBlock");
		user.setCurrentHand(hand);
		if (user instanceof ServerPlayerEntity) {
			YCriteria.PROJECT.trigger((ServerPlayerEntity)user);
		}
		return TypedActionResult.consume(stack);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState bs = world.getBlockState(pos);
		if (bs.isOf(YBlocks.CONTINUOUS_PLATFORM)) {
			if (!world.isClient) {
				if (bs.get(ContinuousPlatformBlock.AGE) == Age.IMMORTAL) {
					world.breakBlock(pos, false);
					world.playSound(null, pos, YSounds.PROJECT, SoundCategory.PLAYERS, 1, 0.5f+(RANDOM.nextFloat()/2));
				} else {
					world.setBlockState(pos, bs.with(ContinuousPlatformBlock.AGE, Age.IMMORTAL));
					if (world instanceof ServerWorld) {
						((ServerWorld)world).spawnParticles(ParticleTypes.CRIT, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 14, 0.5, 0.5, 0.5, 0.05);
					}
					world.playSound(null, pos, YSounds.PROJECT, SoundCategory.PLAYERS, 1, 1.5f+(RANDOM.nextFloat()/2));
				}
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
	
	protected void createPlatform(World world, BlockPos origin) {
		BlockPos.Mutable pos = origin.mutableCopy();
		if (world instanceof ServerWorld) {
			((ServerWorld)world).spawnParticles(ParticleTypes.CRIT, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 1.5, 0.5, 1.5, 0.05);
			((ServerWorld)world).spawnParticles(ParticleTypes.FIREWORK, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 1.5, 0.5, 1.5, 0.05);
		}
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				pos.set(origin).move(x, 0, z);
				BlockState bs = world.getBlockState(pos);
				if (canReplace(bs)) {
					world.setBlockState(pos, YBlocks.CONTINUOUS_PLATFORM.getDefaultState().with(ContinuousPlatformBlock.LOGGED, LogFluid.by(world.getFluidState(pos).getFluid())));
				}
			}
		}
	}

	private boolean canReplace(BlockState bs) {
		return bs.isAir() || bs.getMaterial().isReplaceable() || (bs.isOf(YBlocks.CONTINUOUS_PLATFORM) && bs.get(ContinuousPlatformBlock.AGE) != Age.IMMORTAL);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 200;
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (world.isClient) return;
		if (!stack.hasTag()) stack.setTag(new NbtCompound());
		int ticks = getMaxUseTime(stack)-remainingUseTicks;
		if (ticks != 0 && ticks < 20) return;
		if (ticks > 0) ticks -= 20;
		BlockPos lastPos = stack.getTag().contains("LastBlock") ? NbtHelper.toBlockPos(stack.getTag().getCompound("LastBlock")) : null;
		BlockPos pos = new BlockPos(user.getPos().subtract(0, 1, 0).add(user.getRotationVector().multiply(ticks/2f, ticks/4f, ticks/2f)));
		if (lastPos != null) {
			double len = MathHelper.sqrt(lastPos.getSquaredDistance(pos));
			double diffX = pos.getX()-lastPos.getX();
			double diffY = pos.getY()-lastPos.getY();
			double diffZ = pos.getZ()-lastPos.getZ();
			BlockPos.Mutable mut = new BlockPos.Mutable();
			int count = (int)(len*2);
			for (int i = 0; i < count; i++) {
				double t = (i/(double)count);
				double x = lastPos.getX()+(diffX*t);
				double y = lastPos.getY()+(diffY*t);
				double z = lastPos.getZ()+(diffZ*t);
				mut.set(x, y, z);
				createPlatform(world, mut);
			}
		} else {
			createPlatform(world, pos);
		}
		stack.getTag().put("LastBlock", NbtHelper.fromBlockPos(pos));
		if (ticks == 0) {
			if (user.fallDistance > 20 && user instanceof ServerPlayerEntity) {
				YCriteria.PROJECT_WITH_LONG_FALL.trigger((ServerPlayerEntity)user);
			}
			user.fallDistance = 0;
			if (user.getPos().y < pos.getY()+1) {
				user.requestTeleport(user.getPos().x, pos.getY()+1, user.getPos().z);
			}
			world.playSound(null, user.getX(), user.getY(), user.getZ(), YSounds.PROJECT, SoundCategory.PLAYERS, 0.75f, 1f+(RANDOM.nextFloat()/2));
			world.playSound(null, user.getX(), user.getY(), user.getZ(), YSounds.PROJECT, SoundCategory.PLAYERS, 0.75f, 1.5f+(RANDOM.nextFloat()/2));
		}
		world.playSound(null, user.getX(), user.getY(), user.getZ(), YSounds.PROJECT, SoundCategory.PLAYERS, 0.75f, 0.5f+(RANDOM.nextFloat()/2));
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof PlayerEntity) {
			((PlayerEntity)user).getItemCooldownManager().set(this, 250);
		}
		return stack;
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int ticks = getMaxUseTime(stack)-remainingUseTicks;
		if (ticks > 20) {
			if (user instanceof PlayerEntity) {
				((PlayerEntity)user).getItemCooldownManager().set(this, 250-remainingUseTicks);
			}
		}
	}
	
}

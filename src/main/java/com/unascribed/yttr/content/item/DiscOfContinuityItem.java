package com.unascribed.yttr.content.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class DiscOfContinuityItem extends Item {

	public DiscOfContinuityItem(Settings settings) {
		super(settings);
		DispenserBlock.registerBehavior(this, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction dir = pointer.getBlockState().get(DispenserBlock.FACING);
				Position pos = DispenserBlock.getOutputLocation(pointer);
				ItemStack itemStack = stack.split(1);
				World world = pointer.getWorld();
				double x = pos.getX();
				double y = pos.getY();
				double z = pos.getZ();
				if (dir == Direction.UP) {
					y -= 0.2D;
				} else if (dir != Direction.DOWN) {
					y -= 0.5D;
				}

				ItemEntity ie = new ItemEntity(world, x, y, z, itemStack);
				((Entity)ie).age = 4;
				ie.setNoGravity(true);
				ie.setVelocity(dir.getOffsetX()*0.4, dir.getOffsetY()*0.4, dir.getOffsetZ()*0.4);
				world.spawnEntity(ie);
				return stack;
			}
		});
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient) {
			ItemStack stack = context.getStack().split(1);
			ItemEntity ie = new ItemEntity(context.getWorld(), context.getBlockPos().getX()+0.5, context.getBlockPos().getY()+1, context.getBlockPos().getZ()+0.5, stack);
			ie.setVelocity(0, 0, 0);
			ie.setThrower(context.getPlayer().getUuid());
			((Entity)ie).age = 4;
			ie.setNoGravity(true);
			context.getWorld().spawnEntity(ie);
		}
		return ActionResult.SUCCESS;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (user.isOnGround()) return TypedActionResult.fail(stack);
		if (!world.isClient) {
			user.fallDistance /= 4;
			ItemStack split = stack.split(1);
			ItemEntity ie = new ItemEntity(world, user.getPos().x, user.getPos().y-0.3, user.getPos().z, split);
			ie.setVelocity(0, 0, 0);
			ie.setThrower(user.getUuid());
			((Entity)ie).age = 4;
			ie.setNoGravity(true);
			world.spawnEntity(ie);
		}
		return TypedActionResult.success(stack);
	}

}

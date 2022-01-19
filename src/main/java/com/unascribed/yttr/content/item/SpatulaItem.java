package com.unascribed.yttr.content.item;

import com.unascribed.yttr.mixin.accessor.AccessorBlockSoundGroup;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpatulaItem extends ShovelItem {

	public SpatulaItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
	
	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, attacker.getSoundCategory(), 0.25f, 0.5f);
		attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, attacker.getSoundCategory(), 0.25f, 0.75f);
		serve(target, attacker, 0.75f);
		return super.postHit(stack, target, attacker);
	}
	
	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity attacker, LivingEntity target, Hand hand) {
		serve(target, attacker, 0.3f);
		stack.damage(1, attacker, (t) -> attacker.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
		attacker.getItemCooldownManager().set(this, 10);
		return ActionResult.SUCCESS;
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity p = context.getPlayer();
		BlockPos pos = context.getBlockPos();
		World w = context.getWorld();
		if (pos.getY() < p.getY()) return ActionResult.FAIL;
		if (w.getBlockEntity(pos) != null) return ActionResult.FAIL;
		BlockState bs = w.getBlockState(pos);
		if (bs.getHardness(w, pos) < 0) return ActionResult.FAIL;
		if (!w.isClient) {
			FallingBlockEntity fbe = new FallingBlockEntity(w, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, bs);
			fbe.setVelocity(p.getRotationVector().multiply(p.isSprinting() ? 0.7 : 0.5).add(0, 1, 0));
			p.world.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, p.getSoundCategory(), 1, 1.25f);
			p.world.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, p.getSoundCategory(), 1, 1.75f);
			w.playSound(null, pos, ((AccessorBlockSoundGroup)bs.getSoundGroup()).yttr$getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1);
			w.spawnEntity(fbe);
			p.getItemCooldownManager().set(this, 10);
			context.getStack().damage(1, p, (t) -> p.sendEquipmentBreakStatus(context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
		}
		return ActionResult.SUCCESS;
	}
	
	private void serve(LivingEntity target, LivingEntity attacker, float force) {
		attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1, 1.25f);
		attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1, 1.75f);
		target.takeKnockback(0.5f+force+(attacker.isSprinting() ? 0.3f : 0), attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
		target.setVelocity(target.getVelocity().add(0, force*0.8f, 0));
	}

}

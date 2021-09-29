package com.unascribed.yttr.mixin.effector;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract void travel(Vec3d movementInput);
	
	private UUID yttr$effectorOwner = null;
	
	@Inject(at=@At("TAIL"), method="tick")
	public void tick(CallbackInfo ci) {
		if (world.isClient) return;
		if (world instanceof YttrWorld) {
			YttrWorld yw = (YttrWorld)world;
			if (onGround && fallDistance <= 0) {
				yttr$effectorOwner = null;
			} else if (yttr$effectorOwner == null) {
				BlockPos bp = getBlockPos();
				if (yw.yttr$isPhased(bp)) {
					UUID owner = yw.yttr$getPhaser(bp);
					if (owner != null) {
						yttr$effectorOwner = owner;
					}
				}
			}
		}
	}
	
	@ModifyVariable(at=@At("HEAD"), method="damage", argsOnly=true, ordinal=0)
	public DamageSource modifyDamageSource(DamageSource src) {
		if (src == DamageSource.FALL && yttr$effectorOwner != null) {
			Entity owner = world.getPlayerByUuid(yttr$effectorOwner);
			
			if (owner != null) {
				return new EntityDamageSource("yttr.effector_fall", owner) {
					@Override
					public Text getDeathMessage(LivingEntity entity) {
						// no .item support
						String string = "death.attack." + this.name;
						return new TranslatableText(string, entity.getDisplayName(), this.source.getDisplayName());
					}
				};
			}
		}
		return src;
	}
	
	@Inject(at=@At("HEAD"), method="onDeath")
	public void onDeath(DamageSource source, CallbackInfo ci) {
		if (source instanceof EntityDamageSource && source.getName().equals("yttr.effector_fall")) {
			EntityDamageSource eds = (EntityDamageSource)source;
			if (eds.getAttacker() instanceof ServerPlayerEntity) {
				YCriteria.KILL_WITH_EFFECTOR.trigger((ServerPlayerEntity)eds.getAttacker());
			}
		}
	}
	
}

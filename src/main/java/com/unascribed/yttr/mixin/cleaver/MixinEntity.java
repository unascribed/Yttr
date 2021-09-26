package com.unascribed.yttr.mixin.cleaver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.decor.CleavedBlock;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixinsupport.SlopeStander;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

@Mixin(Entity.class)
public class MixinEntity implements SlopeStander {

	private double yttr$yOffset;
	private double yttr$lastYOffset;
	private float yttr$slopeSteepness;
	
	@Inject(at=@At("HEAD"), method="baseTick")
	public void baseTick(CallbackInfo ci) {
		yttr$lastYOffset = yttr$yOffset;
	}
	
	@Inject(at=@At("HEAD"), method="checkBlockCollision")
	protected void checkBlockCollision(CallbackInfo ci) {
		yttr$yOffset = 0;
		yttr$slopeSteepness = 0;
		Entity self = (Entity)(Object)this;
		Box box = self.getBoundingBox();
		BlockPos bpMin = new BlockPos(box.minX - 0.15, box.minY - 0.15, box.minZ - 0.15);
		BlockPos bpMax = new BlockPos(box.maxX + 0.15, box.maxY + 0.15, box.maxZ + 0.15);
		BlockPos.Mutable mut = new BlockPos.Mutable();
		if (self.world.isRegionLoaded(bpMin, bpMax)) {
			for (int x = bpMin.getX(); x <= bpMax.getX(); ++x) {
				for (int y = bpMin.getY(); y <= bpMax.getY(); ++y) {
					for (int z = bpMin.getZ(); z <= bpMax.getZ(); ++z) {
						mut.set(x, y, z);
						BlockState bs = self.world.getBlockState(mut);
						if (bs.isOf(YBlocks.CLEAVED_BLOCK)) {
							try {
								((CleavedBlock)bs.getBlock()).onEntityNearby(bs, self.world, mut, self);
							} catch (Throwable t) {
								CrashReport report = CrashReport.create(t, "[Yttr] Performing cleaved block slope adjustment");
								CrashReportSection section = report.addElement("Block being collided with");
								CrashReportSection.addBlockInfo(section, mut, bs);
								throw new CrashException(report);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public double yttr$getYOffset() {
		return yttr$yOffset;
	}

	@Override
	public void yttr$setYOffset(double yOffset) {
		yttr$yOffset = yOffset;
	}
	
	@Override
	public double yttr$getLastYOffset() {
		return yttr$lastYOffset;
	}

	@Override
	public float yttr$getSlopeSteepness() {
		return yttr$slopeSteepness;
	}

	@Override
	public void yttr$setSlopeSteepness(float steepness) {
		yttr$slopeSteepness = steepness;
	}

}

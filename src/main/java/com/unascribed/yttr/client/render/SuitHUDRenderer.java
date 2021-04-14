package com.unascribed.yttr.client.render;

import java.util.Locale;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.block.void_.DivingPlateBlock;
import com.unascribed.yttr.block.void_.VoidGeyserBlockEntity;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.item.SuitArmorItem;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.SuitResource;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;

public class SuitHUDRenderer extends IHasAClient {

	private static SuitRenderer diveReadyRenderer = new SuitRenderer();
	private static int diveReadyTime = 0;
	
	public static void render(MatrixStack matrices, float delta) {
		if (diveReadyTime > 0) {
			if (diveReadyRenderer == null) {
				diveReadyRenderer = new SuitRenderer();
			}
			diveReadyRenderer.setUp();
			diveReadyRenderer.setColor(LampBlockItem.getColor(mc.player.getEquippedStack(EquipmentSlot.HEAD)));
			int width = mc.getWindow().getScaledWidth();
			String text = "hold sneak to dive";
			ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
			if (chest.getItem() instanceof SuitArmorItem) {
				SuitArmorItem sai = (SuitArmorItem)chest.getItem();
				int resourceBarY = 32;
				for (SuitResource res : SuitResource.VALUES) {
					int len = res.name().length()*6;
					
					String name = res.name().toLowerCase(Locale.ROOT);
					
					float amt = sai.getResourceAmount(chest, res);
					float a = amt/res.getMaximum();
					if (a < 0.5f) {
						diveReadyRenderer.drawElement(matrices, name+"-warning", width-96, resourceBarY-2, 0, 18, 11, 12, delta);
					}
					if (a <= 0 && res != SuitResource.FUEL) {
						text = "hold sneak to die";
					}
					
					diveReadyRenderer.drawText(matrices, name, width-len-16, resourceBarY, delta);
					diveReadyRenderer.drawBar(matrices, name, width-96, resourceBarY+12, a, true, delta);
					resourceBarY += 24;
				}
			}
			diveReadyRenderer.drawText(matrices, text, width-16-(text.length()*6), 12, delta);
			diveReadyRenderer.tearDown();
		}
	}

	public static void tick() {
		if (mc.player != null && Yttr.isWearingFullSuit(mc.player) && Yttr.isStandingOnDivingPlate(mc.player)) {
			VoidGeyserBlockEntity geyser = DivingPlateBlock.findClosestGeyser(mc.world, mc.player.getBlockPos());
			if (geyser != null && mc.player.getBoundingBox().intersects(new Box(geyser.getPos()).expand(5))) {
				diveReadyTime++;
				if (diveReadyRenderer != null) diveReadyRenderer.tick();
			} else {
				diveReadyTime = 0;
				diveReadyRenderer = null;
			}
		} else {
			diveReadyTime = 0;
			diveReadyRenderer = null;
		}
	}
	
}

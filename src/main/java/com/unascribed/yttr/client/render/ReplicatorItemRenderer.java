package com.unascribed.yttr.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.IHasAClient;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class ReplicatorItemRenderer extends IHasAClient {

	public static void render(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		CompoundTag entityTag = stack.getSubTag("BlockEntityTag");
		ItemStack item = ItemStack.EMPTY;
		int seed = stack.hashCode();
		if (entityTag != null) {
			item = ItemStack.fromTag(entityTag.getCompound("Item"));
			seed = entityTag.getInt("Seed");
		}
		boolean fudge = item.isEmpty() && (mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.FIRST_PERSON_RIGHT_HAND);
		if (fudge) {
			// ?????
			matrices.push();
			matrices.scale(0, 0, 0);
			item = new ItemStack(Items.APPLE);
			// ¿¿¿¿¿
		}
		ReplicatorRenderer.render(matrices, mc.world == null ? 0 : mc.getTickDelta(), seed, item, BlockPos.ORIGIN, mc.world == null ? 0 : (int)mc.world.getTime(), null, mode == Mode.GUI ? -1 : 0);
		if (fudge) {
			matrices.pop();
		}
		RenderSystem.depthMask(false);
		ReplicatorRenderer.render(matrices, mc.world == null ? 0 : mc.getTickDelta(), seed, item, BlockPos.ORIGIN, mc.world == null ? 0 : (int)mc.world.getTime(), null, 1);
		RenderSystem.depthMask(true);
	}
	
}

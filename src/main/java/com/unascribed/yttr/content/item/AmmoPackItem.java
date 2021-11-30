package com.unascribed.yttr.content.item;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class AmmoPackItem extends TrinketItem {

	public AmmoPackItem(Settings settings) {
		super(settings);
	}
	
	public int getSize(ItemStack pack) {
		return 6;
	}
	
	public void setStack(ItemStack pack, int i, ItemStack stack) {
		int size = getSize(pack);
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException(""+i);
		if (!pack.hasTag()) pack.setTag(new NbtCompound());
		NbtList inv = pack.getTag().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		inv.set(i, stack.isEmpty() ? new NbtCompound() : stack.writeNbt(new NbtCompound()));
		pack.getTag().put("Contents", inv);
	}

	public ItemStack getStack(ItemStack pack, int i) {
		int size = getSize(pack);
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException(""+i);
		if (!pack.hasTag()) return ItemStack.EMPTY;
		NbtList inv = pack.getTag().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		NbtCompound comp = inv.getCompound(i);
		if (comp.isEmpty()) return ItemStack.EMPTY;
		return ItemStack.fromNbt(comp);
	}
	
	public void clear(ItemStack pack) {
		if (pack.hasTag()) pack.getTag().remove("Contents");
	}
	
	public Inventory asInventory(ItemStack pack) {
		return new Inventory() {
			
			@Override
			public void clear() {
				AmmoPackItem.this.clear(pack);
			}
			
			@Override
			public int size() {
				return AmmoPackItem.this.getSize(pack);
			}
			
			@Override
			public void setStack(int slot, ItemStack stack) {
				AmmoPackItem.this.setStack(pack, slot, stack);
			}
			
			@Override
			public ItemStack getStack(int slot) {
				return AmmoPackItem.this.getStack(pack, slot);
			}
			
			@Override
			public ItemStack removeStack(int slot, int amount) {
				ItemStack content = getStack(slot);
				ItemStack res = content.split(amount);
				setStack(slot, content);
				return res;
			}
			
			@Override
			public ItemStack removeStack(int slot) {
				ItemStack content = getStack(slot);
				setStack(slot, ItemStack.EMPTY);
				return content;
			}
			
			@Override
			public void markDirty() {
			}
			
			@Override
			public boolean isEmpty() {
				for (int i = 0; i < size() ; i++) {
					if (!getStack(i).isEmpty()) return false;
				}
				return true;
			}
			
			@Override
			public boolean canPlayerUse(PlayerEntity player) {
				return true;
			}
		};
	}
	
	protected static void ensureSize(NbtList li, int size) {
		if (li.size() < size) {
			for (int j = 0; j < size-li.size(); j++) {
				li.add(new NbtCompound());
			}
		}
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.CHEST) && slot.equals(Slots.BACKPACK);
	}
	
	@Environment(EnvType.CLIENT)
	private static final ModelIdentifier MODEL = new ModelIdentifier("yttr:ammo_pack_model#inventory");
	
	@Environment(EnvType.CLIENT)
	@Override
	public void render(String slot, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light,
			PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float headYaw, float headPitch) {
		BakedModel bm = MinecraftClient.getInstance().getBakedModelManager().getModel(MODEL);
		matrices.push();
			model.body.rotate(matrices);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
			matrices.translate(-8/16f, -12/16f, 2/16f);
			VertexConsumer vc = vertexConsumer.getBuffer(RenderLayer.getEntityCutout(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
			for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
				vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
			}
			for (Direction d : Direction.values()) {
				int i = d.ordinal();
				if (i != 1) continue;
				for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), d, RANDOM)) {
					vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
				}
			}
		matrices.pop();
	}

}

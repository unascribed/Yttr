package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class LevitationChamberBlockEntity extends BlockEntity implements Tickable, SideyInventory, NamedScreenHandlerFactory, DelegatingInventory {

	private final SimpleInventory inv = new SimpleInventory(5);
	
	public int age;
	private int cooldown = 0;
	
	public LevitationChamberBlockEntity() {
		super(YBlockEntities.LEVITATION_CHAMBER);
		inv.addListener((i) -> markDirty());
	}
	
	@Override
	public void tick() {
		age++;
		if (cooldown-- <= 0 && !isEmpty()) {
			for (int i = 0; i < size(); i++) {
				ItemStack is = getStack(i);
				if (!is.isEmpty()) {
					ItemStack transfer = is.copy();
					transfer.setCount(1);
					if (ChuteBlockEntity.transfer(world, pos, Direction.UP, transfer, false)) {
						is.decrement(1);
						setStack(i, is);
						cooldown = 4;
						break;
					}
				}
			}
		}
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		tag = super.writeNbt(tag);
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("Cooldown", cooldown);
		return tag;
	}
	
	@Override
	public Inventory getDelegateInv() {
		return inv;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return inv.canPlayerUse(player);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return dir == Direction.UP;
	}
	
	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return true;
	}
	
	@Override
	public boolean canAccess(int slot, Direction side) {
		return true;
	}
	
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new HopperScreenHandler(syncId, inv, this);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("block.yttr.levitation_chamber");
	}
	
}

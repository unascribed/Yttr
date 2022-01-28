package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.inventory.ProjectTableScreenHandler;
import com.unascribed.yttr.util.DelegatingInventory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProjectTableBlockEntity extends LockableContainerBlockEntity implements DelegatingInventory {

	private final SimpleInventory inv = new SimpleInventory(27);
	
	public ProjectTableBlockEntity() {
		super(YBlockEntities.PROJECT_TABLE);
		inv.addListener(i -> markDirty());
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.put("Inventory", Yttr.serializeInv(inv));
		return nbt;
	}
	
	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return player.getPos().squaredDistanceTo(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5) < 5*5;
	}

	@Override
	public Inventory getDelegateInv() {
		return inv;
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("block.yttr.project_table");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new ProjectTableScreenHandler(syncId, this, playerInventory, ScreenHandlerContext.create(world, pos));
	}

}

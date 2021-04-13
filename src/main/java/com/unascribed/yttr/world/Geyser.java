package com.unascribed.yttr.world;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public final class Geyser {

	public final UUID id;
	public final BlockPos pos;
	public transient final ChunkPos chunkPos;
	public String name;
	
	public Geyser(UUID id, BlockPos pos, String name) {
		this.id = id;
		this.pos = pos.toImmutable();
		this.chunkPos = new ChunkPos(pos);
		this.name = name;
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putUuid("ID", id);
		tag.put("Pos", NbtHelper.fromBlockPos(pos));
		tag.putString("Name", name);
		return tag;
	}
	
	public static Geyser fromTag(CompoundTag tag) {
		return new Geyser(
				tag.getUuid("ID"),
				NbtHelper.toBlockPos(tag.getCompound("Pos")),
				tag.getString("Name")
		);
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeUuid(id);
		buf.writeBlockPos(pos);
		buf.writeString(name);
	}
	
	public static Geyser read(PacketByteBuf buf) {
		return new Geyser(
				buf.readUuid(),
				buf.readBlockPos(),
				buf.readString()
		);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Geyser other = (Geyser) obj;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Geyser [id=" + id + ", pos=" + pos + ", name=" + name + "]";
	}
	
}

package com.unascribed.yttr.world;

import java.util.UUID;

import com.unascribed.yttr.network.concrete.ImmutableMarshallable;
import com.unascribed.yttr.util.math.Vec2i;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public final class Geyser implements ImmutableMarshallable {

	public final UUID id;
	public final BlockPos pos;
	public transient final ChunkPos chunkPos;
	public transient final Vec2i regionPos;
	public String name;
	
	public Geyser(UUID id, BlockPos pos, String name) {
		this.id = id;
		this.pos = pos.toImmutable();
		this.chunkPos = new ChunkPos(pos);
		this.regionPos = new Vec2i(pos.getX()/512, pos.getZ()/512);
		this.name = name;
	}
	
	public NbtCompound toTag() {
		NbtCompound tag = new NbtCompound();
		tag.putUuid("ID", id);
		tag.put("Pos", NbtHelper.fromBlockPos(pos));
		tag.putString("Name", name);
		return tag;
	}
	
	public static Geyser fromTag(NbtCompound tag) {
		return new Geyser(
				tag.getUuid("ID"),
				NbtHelper.toBlockPos(tag.getCompound("Pos")),
				tag.getString("Name")
		);
	}

	// implements ImmutableMarshallable {
	@Override
	public void writeToNetwork(PacketByteBuf buf) {
		buf.writeUuid(id);
		buf.writeBlockPos(pos);
		buf.writeString(name);
	}
	
	public static Geyser readFromNetwork(PacketByteBuf buf) {
		return new Geyser(
				buf.readUuid(),
				buf.readBlockPos(),
				buf.readString()
		);
	}
	// }

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

package com.unascribed.yttr.mechanics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;

import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.util.EquipmentSlots;

import com.google.common.base.Predicates;
import com.google.common.io.MoreFiles;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class VoidLogic {

	private static final DateFormat fmt = new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss");
	
	public static void doVoid(PlayerEntity user, World _world, Vec3d _pos, int r) {
		// TODO this really needs some optimizing; the NBT undo files are oversized and the index
		// can't be used efficiently. not going to make a huge deal out of it right now
		if (!(_world instanceof ServerWorld)) return;
		try {
			ServerWorld world = (ServerWorld)_world;
			String dim = world.getRegistryKey().getValue().toString().replace(':', '_').replace('/', '_');
			String undoName = dim+"_"+fmt.format(new Date())+"_"+user.getGameProfile().getName()+"_"+ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeFloat((float)_pos.x);
			buf.writeFloat((float)_pos.y);
			buf.writeFloat((float)_pos.z);
			buf.writeFloat(r+0.5f);
			Packet<?> pkt = ServerPlayNetworking.createS2CPacket(new Identifier("yttr", "void_ball"), buf);
			for (PlayerEntity pe : world.getPlayers()) {
				if (pe instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)pe).networkHandler.sendPacket(pkt);
				}
			}
			world.playSound(null, _pos.x, _pos.y, _pos.z, YSounds.VOID, SoundCategory.PLAYERS, 4, 1);
			BlockPos pos = new BlockPos(_pos);
			Path root = getUndoDirectory(world.getServer());
			Path out = root.resolve(undoName+".dat");
			MoreFiles.createParentDirectories(out);
			Path indexFile = root.resolve("index.dat");
			CompoundTag index = Files.exists(indexFile) ? NbtIo.readCompressed(indexFile.toFile()) : new CompoundTag();
			
			CompoundTag byUser = index.getCompound("ByUser");
			String id = user.getGameProfile().getId().toString();
			CompoundTag userData = byUser.getCompound(id);
			userData.putString("Username", user.getGameProfile().getName());
			ListTag userList = userData.getList("List", NbtType.STRING);
			userList.add(StringTag.of(undoName));
			userData.put("List", userList);
			byUser.put(id, userData);
			index.put("ByUser", byUser);
			
			CompoundTag byChunk = index.getCompound("ByChunk");
			ChunkPos chunkPos = new ChunkPos(pos);
			String chunkKey = chunkPos.x+" "+chunkPos.z;
			ListTag chunkList = byUser.getList(chunkKey, NbtType.COMPOUND);
			CompoundTag chunkListEntry = new CompoundTag();
			chunkListEntry.putByte("HPos", (byte)(((pos.getX()&0xF)<<4)|(pos.getZ()&0xF)));
			chunkListEntry.putShort("YPos", (short)pos.getY());
			chunkListEntry.putString("Dim", world.getRegistryKey().getValue().toString());
			chunkListEntry.putString("Name", undoName);
			chunkList.add(chunkListEntry);
			byChunk.put(chunkKey, chunkList);
			index.put("ByChunk", byChunk);
			
			CompoundTag data = new CompoundTag();
			data.putIntArray("Pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
			data.putString("Dim", world.getRegistryKey().getValue().toString());
			ListTag blocks = new ListTag();
			data.put("Blocks", blocks);
			BlockPos.Mutable bp = new BlockPos.Mutable();
			for (int y = -r; y <= r; y++) {
				for (int x = -r; x <= r; x++) {
					for (int z = -r; z <= r; z++) {
						bp.set(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
						if (pos.getSquaredDistance(bp.getX(), bp.getY(), bp.getZ(), true) < r*r) {
							BlockState bs = world.getBlockState(bp);
							if (bs.getHardness(world, bp) < 0) continue;
							BlockEntity be = world.getBlockEntity(bp);
							CompoundTag block = new CompoundTag();
							block.putByteArray("Pos", new byte[] {(byte)x, (byte)y, (byte)z});
							block.putString("Block", Registry.BLOCK.getId(bs.getBlock()).toString());
							if (!bs.getEntries().isEmpty()) {
								CompoundTag state = new CompoundTag();
								for (Map.Entry<Property, Comparable<?>> en : (Set<Map.Entry<Property, Comparable<?>>>)(Set)(bs.getEntries().entrySet())) {
									state.putString(en.getKey().getName(), en.getKey().name(en.getValue()));
								}
								block.put("State", state);
							}
							if (be != null) {
								block.put("Entity", be.toTag(new CompoundTag()));
							}
							blocks.add(block);
						}
					}
				}
			}
			NbtIo.writeCompressed(data, out.toFile());
			NbtIo.writeCompressed(index, indexFile.toFile());
			for (int y = -r; y <= r; y++) {
				for (int x = -r; x <= r; x++) {
					for (int z = -r; z <= r; z++) {
						bp.set(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
						if (pos.getSquaredDistance(bp.getX(), bp.getY(), bp.getZ(), true) < r*r) {
							BlockState bs = world.getBlockState(bp);
							if (bs.getHardness(world, bp) < 0) continue;
							world.removeBlockEntity(bp);
							world.setBlockState(bp, Blocks.VOID_AIR.getDefaultState());
						}
					}
				}
			}
			Box box = new Box(_pos.x-r, _pos.y-r, _pos.z-r, _pos.x+r, _pos.y+r, _pos.z+r);
			for (Entity e : world.getEntitiesByClass(Entity.class, box, Predicates.alwaysTrue())) {
				double d = _pos.squaredDistanceTo(e.getPos());
				if (d < r*r) {
					float dmg = (float) ((r*r)-d);
					e.damage(new SolventDamageSource(0), dmg);
					if (e instanceof LivingEntity) {
						LivingEntity le = (LivingEntity)e;
						for (EquipmentSlot es : EquipmentSlots.ARMOR) {
							le.getEquippedStack(es).damage((int)dmg, le, (blah) -> {
								le.sendEquipmentBreakStatus(es);
							});
						}
					}
				}
			}
			LogManager.getLogger("Yttr").info("{} performed a {} radius void at {}, {}, {} in {}. Undo with /yttr:void_undo just {} or undo all voids by this player with /yttr:void_undo by {}",
					user.getGameProfile().getName(), r, pos.getX(), pos.getY(), pos.getZ(), world.getRegistryKey().getValue(), undoName, user.getGameProfile().getName());
		} catch (IOException e) {
			LogManager.getLogger("Yttr").warn("Failed to void", e);
		}
	}

	public static Path getUndoDirectory(MinecraftServer server) {
		return server.getSavePath(WorldSavePath.ROOT).resolve("yttr_void_undo");
	}
	
}

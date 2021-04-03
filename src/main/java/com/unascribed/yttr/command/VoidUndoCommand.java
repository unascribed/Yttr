package com.unascribed.yttr.command;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import com.unascribed.yttr.world.VoidLogic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.MoreFiles;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class VoidUndoCommand {

	public static LiteralArgumentBuilder<ServerCommandSource> create() {
		return CommandManager.literal("yttr:void_undo")
				.requires((scs) -> scs.hasPermissionLevel(4))
				.then(CommandManager.literal("clean")
					.executes((ctx) -> {
						try {
							Path dir = VoidLogic.getUndoDirectory(ctx.getSource().getMinecraftServer());
							long count;
							if (Files.exists(dir)) {
								count = Files.list(dir).count();
								MoreFiles.deleteRecursively(dir);
							} else {
								count = 0;
							}
							ctx.getSource().sendFeedback(new TranslatableText("commands.yttr.void_undo.clean", count), true);
						} catch (IOException e) {
							LogManager.getLogger("Yttr").warn("Failed to clean undos", e);
							throw new UncheckedIOException(e);
						}
						return 1;
					})
				)
				.then(CommandManager.literal("just")
					.then(CommandManager.argument("file", StringArgumentType.greedyString())
						.suggests((ctx, bldr) -> {
							ServerCommandSource scs = ctx.getSource();
							Path root = VoidLogic.getUndoDirectory(scs.getMinecraftServer());
							String dim = scs.getWorld().getRegistryKey().getValue().toString();
							BlockPos pos = new BlockPos(scs.getPosition());
							ChunkPos chunkPos = new ChunkPos(pos);
							return CompletableFuture.supplyAsync(() -> {
								try {
									Path indexFile = root.resolve("index.dat");
									if (Files.exists(indexFile)) {
										CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
										CompoundTag byChunk = index.getCompound("ByChunk");
										List<Pair<Double, Runnable>> suggestorsByDistance = Lists.newArrayList();
										for (int x = -2; x <= 2; x++) {
											for (int z = -2; z <= 2; z++) {
												String s = (chunkPos.x+x)+" "+(chunkPos.z+z);
												if (byChunk.contains(s, NbtType.LIST)) {
													ListTag list = byChunk.getList(s, NbtType.COMPOUND);
													for (int i = 0; i < list.size(); i++) {
														CompoundTag entry = list.getCompound(i);
														if (!entry.getString("Dim").equals(dim)) continue;
														int hpos = entry.getByte("HPos")&0xFF;
														BlockPos entryPos = new ChunkPos(chunkPos.x+x, chunkPos.z+z).getStartPos().add((hpos>>4)&0xF, entry.getInt("YPos"), hpos&0xF);
														double dist = entryPos.getSquaredDistance(pos);
														if (dist < 32*32) {
															suggestorsByDistance.add(Pair.of(dist, () -> bldr.suggest(entry.getString("Name"), new TranslatableText("commands.yttr.void_undo.location", entryPos.getX(), entryPos.getY(), entryPos.getZ(), (int)MathHelper.sqrt(dist)))));
														}
													}
												}
											}
										}
										Collections.sort(suggestorsByDistance, (a, b) -> Double.compare(a.getFirst(), b.getFirst()));
										for (Pair<Double, Runnable> p : suggestorsByDistance) {
											p.getSecond().run();
										}
									}
									return bldr.build();
								} catch (IOException e) {
									LogManager.getLogger("Yttr").warn("Failed to suggest undos", e);
									throw new UncheckedIOException(e);
								}
							});
						})
						.executes((ctx) -> {
							Path root = VoidLogic.getUndoDirectory(ctx.getSource().getMinecraftServer());
							Path indexFile = root.resolve("index.dat");
							String fname = StringArgumentType.getString(ctx, "file");
							Path file = root.resolve(fname+".dat");
							if (Files.exists(file)) {
								try {
									CompoundTag data = NbtIo.readCompressed(file.toFile());
									int count = undo(ctx.getSource().getMinecraftServer(), data);
									if (Files.exists(indexFile)) {
										CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
										removeFromIndex(index, Collections.singleton(fname));
										NbtIo.writeCompressed(index, indexFile.toFile());
										Files.delete(file);
									}
									ctx.getSource().sendFeedback(new TranslatableText("commands.yttr.void_undo.success", count), true);
								} catch (IOException e) {
									LogManager.getLogger("Yttr").warn("Failed to undo", e);
									throw new UncheckedIOException(e);
								}
							} else {
								throw new CommandException(new TranslatableText("commands.yttr.void_undo.not_found"));
							}
							return 1;
						})
					)
				)
				.then(CommandManager.literal("by")
					.then(CommandManager.argument("user", StringArgumentType.greedyString())
						.suggests((ctx, bldr) -> {
							ServerCommandSource scs = ctx.getSource();
							Path root = VoidLogic.getUndoDirectory(scs.getMinecraftServer());
							return CompletableFuture.supplyAsync(() -> {
								try {
									Path indexFile = root.resolve("index.dat");
									if (Files.exists(indexFile)) {
										CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
										CompoundTag byUser = index.getCompound("ByUser");
										for (String k : byUser.getKeys()) {
											CompoundTag tag = byUser.getCompound(k);
											bldr.suggest(tag.getString("Username"), new LiteralText(k));
										}
									}
									return bldr.build();
								} catch (IOException e) {
									LogManager.getLogger("Yttr").warn("Failed to suggest undos", e);
									throw new UncheckedIOException(e);
								}
							});
						})
						.executes((ctx) -> {
							Path root = VoidLogic.getUndoDirectory(ctx.getSource().getMinecraftServer());
							Path indexFile = root.resolve("index.dat");
							String user = StringArgumentType.getString(ctx, "user");
							Set<String> fnames = Sets.newHashSet();
							try {
								if (Files.exists(indexFile)) {
									CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
									CompoundTag byUser = index.getCompound("ByUser");
									for (String k : byUser.getKeys()) {
										CompoundTag tag = byUser.getCompound(k);
										if (k.equals(user) || tag.getString("Username").equals(user)) {
											ListTag list = tag.getList("List", NbtType.STRING);
											for (int i = 0; i < list.size(); i++) {
												fnames.add(list.getString(i));
											}
										}
									}
									if (!fnames.isEmpty()) {
										int count = 0;
										int success = 0;
										for (String fname : fnames) {
											try {
												Path file = root.resolve(fname+".dat");
												CompoundTag data = NbtIo.readCompressed(file.toFile());
												count += undo(ctx.getSource().getMinecraftServer(), data);
												success++;
												if (Files.exists(indexFile)) {
													Files.delete(file);
												}
											} catch (IOException e) {
												LogManager.getLogger("Yttr").warn("Failed to undo "+fname, e);
											}
										}
										ctx.getSource().sendFeedback(new TranslatableText("commands.yttr.void_undo.success_multi", count, success), true);
										removeFromIndex(index, fnames);
										NbtIo.writeCompressed(index, indexFile.toFile());
									} else {
										throw new CommandException(new TranslatableText("commands.yttr.void_undo.not_found"));
									}
								} else {
									throw new CommandException(new TranslatableText("commands.yttr.void_undo.not_found"));
								}
							} catch (IOException e) {
								LogManager.getLogger("Yttr").warn("Failed to read/update index", e);
								throw new UncheckedIOException(e);
							}
							return 1;
						})
					)
				);
	}

	private static int undo(MinecraftServer server, CompoundTag data) {
		int count = 0;
		Identifier dim = new Identifier(data.getString("Dim"));
		World world = server.getWorld(RegistryKey.of(Registry.DIMENSION, dim));
		if (world == null) {
			throw new CommandException(new TranslatableText("commands.yttr.void_undo.no_world", dim.toString()));
		}
		int[] posArr = data.getIntArray("Pos");
		BlockPos pos = new BlockPos(posArr[0], posArr[1], posArr[2]);
		ListTag blocks = data.getList("Blocks", NbtType.COMPOUND);
		BlockPos.Mutable mut = new BlockPos.Mutable();
		for (int i = 0; i < blocks.size(); i++) {
			CompoundTag block = blocks.getCompound(i);
			byte[] posOfs = block.getByteArray("Pos");
			mut.set(pos.getX()+posOfs[0], pos.getY()+posOfs[1], pos.getZ()+posOfs[2]);
			Block b = Registry.BLOCK.get(new Identifier(block.getString("Block")));
			if (b == null) continue;
			BlockState bs = b.getDefaultState();
			if (block.contains("State", NbtType.COMPOUND)) {
				CompoundTag state = block.getCompound("State");
				for (Property<?> prop : bs.getProperties()) {
					if (state.contains(prop.getName(), NbtType.STRING)) {
						bs = setParseProperty(bs, prop, state.getString(prop.getName()));
					}
				}
			}
			if (bs.isAir() && !world.isAir(mut)) continue;
			if (world.setBlockState(mut, bs)) count++;
			if (block.contains("Entity", NbtType.COMPOUND)) {
				CompoundTag tag = block.getCompound("Entity");
				if (world.getBlockEntity(mut) != null) {
					world.getBlockEntity(mut).fromTag(bs, tag);
				} else {
					world.setBlockEntity(mut, BlockEntity.createFromTag(bs, tag));
				}
			}
		}
		return count;
	}

	private static void removeFromIndex(CompoundTag index, Set<String> fnames) {
		CompoundTag byChunk = index.getCompound("ByChunk");
		for (String k : ImmutableList.copyOf(byChunk.getKeys())) {
			ListTag list = byChunk.getList(k, NbtType.COMPOUND);
			for (int i = list.size()-1; i >= 0; i--) {
				if (fnames.contains(list.getCompound(i).getString("Name"))) {
					list.remove(i);
				}
			}
			if (list.isEmpty()) {
				byChunk.remove(k);
			}
		}
		CompoundTag byUser = index.getCompound("ByUser");
		for (String k : ImmutableList.copyOf(byUser.getKeys())) {
			CompoundTag userData = byUser.getCompound(k);
			ListTag list = userData.getList("List", NbtType.STRING);
			for (int i = list.size()-1; i >= 0; i--) {
				if (fnames.contains(list.getString(i))) {
					list.remove(i);
				}
			}
			if (list.isEmpty()) {
				byUser.remove(k);
			}
		}
	}

	private static <T extends Comparable<T>> BlockState setParseProperty(BlockState bs, Property<T> prop, String val) {
		Optional<T> opt = prop.parse(val);
		if (!opt.isPresent()) return bs;
		return bs.with(prop, opt.get());
	}

	public static void init() {
		
	}
	
}

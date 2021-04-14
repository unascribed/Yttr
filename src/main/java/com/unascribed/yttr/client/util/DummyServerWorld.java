package com.unascribed.yttr.client.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class DummyServerWorld extends ServerWorld {

	public DummyServerWorld(MinecraftServer server, Executor workerExecutor,
			Session session, ServerWorldProperties properties,
			RegistryKey<World> registryKey, DimensionType dimensionType,
			WorldGenerationProgressListener worldGenerationProgressListener,
			ChunkGenerator chunkGenerator, boolean debugWorld, long l,
			List<Spawner> list, boolean bl) {
		super(server, workerExecutor, session, properties, registryKey, dimensionType,
				worldGenerationProgressListener, chunkGenerator, debugWorld, l, list,
				bl);
		// WILL NOT BE CALLED
	}
	
	public void init() {
		isClient = true;
		random = new Random();
	}
	
	@Override
	public Entity getEntity(UUID uUID) {
		return getDelegate().getPlayerByUuid(uUID);
	}

	private ClientWorld getDelegate() {
		return MinecraftClient.getInstance().world;
	}

	@Override
	public float getMoonSize() {
		return getDelegate().getMoonSize();
	}

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return getDelegate().getBrightness(direction, shaded);
	}

	@Override
	public float getSkyAngle(float tickDelta) {
		return getDelegate().getSkyAngle(tickDelta);
	}

	@Override
	public long getLunarTime() {
		return getDelegate().getLunarTime();
	}

	@Override
	public int getLightLevel(LightType type, BlockPos pos) {
		return getDelegate().getLightLevel(type, pos);
	}

	@Override
	public int getMoonPhase() {
		return getDelegate().getMoonPhase();
	}

	@Override
	public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
		return getDelegate().getBaseLightLevel(pos, ambientDarkness);
	}

	@Override
	public Stream<VoxelShape> getEntityCollisions(Entity entity, Box box,
			Predicate<Entity> predicate) {
		return getDelegate().getEntityCollisions(entity, box, predicate);
	}

	@Override
	public Difficulty getDifficulty() {
		return getDelegate().getDifficulty();
	}

	@Override
	public boolean isSkyVisible(BlockPos pos) {
		return getDelegate().isSkyVisible(pos);
	}

	@Override
	public boolean canPlace(BlockState state, BlockPos pos,
			ShapeContext context) {
		return false;
	}

	@Override
	public int getLuminance(BlockPos pos) {
		return getDelegate().getLuminance(pos);
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return getDelegate().isChunkLoaded(chunkX, chunkZ);
	}

	@Override
	public boolean intersectsEntities(Entity entity, VoxelShape shape) {
		return getDelegate().intersectsEntities(entity, shape);
	}

	@Override
	public int getMaxLightLevel() {
		return getDelegate().getMaxLightLevel();
	}

	@Override
	public int getHeight() {
		return getDelegate().getHeight();
	}

	@Override
	public void updateNeighbors(BlockPos pos, Block block) {
		getDelegate().updateNeighbors(pos, block);
	}

	@Override
	public BlockPos getTopPosition(Type type, BlockPos pos) {
		return getDelegate().getTopPosition(type, pos);
	}

	@Override
	public Stream<BlockState> method_29546(Box box) {
		return getDelegate().method_29546(box);
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return getDelegate().getBiome(pos);
	}

	@Override
	public boolean intersectsEntities(Entity entity) {
		return getDelegate().intersectsEntities(entity);
	}

	@Override
	public BlockHitResult raycast(RaycastContext context) {
		return getDelegate().raycast(context);
	}

	@Override
	public DynamicRegistryManager getRegistryManager() {
		return getDelegate().getRegistryManager();
	}

	@Override
	public Stream<BlockState> method_29556(Box box) {
		return getDelegate().method_29556(box);
	}

	@Override
	public Optional<RegistryKey<Biome>> getBiomeKey(BlockPos blockPos) {
		return getDelegate().getBiomeKey(blockPos);
	}

	@Override
	public boolean isSpaceEmpty(Box box) {
		return getDelegate().isSpaceEmpty(box);
	}

	@Override
	public void syncWorldEvent(PlayerEntity player, int eventId, BlockPos pos,
			int data) {
		getDelegate().syncWorldEvent(player, eventId, pos, data);
	}

	@Override
	public boolean isSpaceEmpty(Entity entity) {
		return getDelegate().isSpaceEmpty(entity);
	}

	@Override
	public int getDimensionHeight() {
		return getDelegate().getDimensionHeight();
	}

	@Override
	public void syncWorldEvent(int eventId, BlockPos pos, int data) {
		getDelegate().syncWorldEvent(eventId, pos, data);
	}

	@Override
	public boolean isSpaceEmpty(Entity entity, Box box) {
		return getDelegate().isSpaceEmpty(entity, box);
	}

	@Override
	public int getColor(BlockPos pos, ColorResolver colorResolver) {
		return getDelegate().getColor(pos, colorResolver);
	}

	@Override
	public boolean isSpaceEmpty(Entity entity, Box box,
			Predicate<Entity> predicate) {
		return getDelegate().isSpaceEmpty(entity, box, predicate);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return getDelegate().getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
	}

	@Override
	public Stream<VoxelShape> getCollisions(Entity entity, Box box,
			Predicate<Entity> predicate) {
		return getDelegate().getCollisions(entity, box, predicate);
	}

	@Override
	public List<Entity> getOtherEntities(Entity except, Box box) {
		return getDelegate().getOtherEntities(except, box);
	}

	@Override
	public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
		return getDelegate().getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
	}

	@Override
	public Stream<VoxelShape> getBlockCollisions(Entity entity, Box box) {
		return getDelegate().getBlockCollisions(entity, box);
	}

	@Override
	public boolean isBlockSpaceEmpty(Entity entity, Box box,
			BiPredicate<BlockState, BlockPos> biPredicate) {
		return getDelegate().isBlockSpaceEmpty(entity, box, biPredicate);
	}

	@Override
	public BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos,
			VoxelShape shape, BlockState state) {
		return getDelegate().raycastBlock(start, end, pos, shape, state);
	}

	@Override
	public Stream<VoxelShape> getBlockCollisions(Entity entity, Box box,
			BiPredicate<BlockState, BlockPos> biPredicate) {
		return getDelegate().getBlockCollisions(entity, box, biPredicate);
	}

	@Override
	public boolean isAir(BlockPos pos) {
		return getDelegate().isAir(pos);
	}

	@Override
	public boolean isSkyVisibleAllowingSea(BlockPos pos) {
		return getDelegate().isSkyVisibleAllowingSea(pos);
	}

	@Override
	public double getDismountHeight(VoxelShape blockCollisionShape,
			Supplier<VoxelShape> belowBlockCollisionShapeGetter) {
		return getDelegate().getDismountHeight(blockCollisionShape,
				belowBlockCollisionShapeGetter);
	}

	@Override
	public float getBrightness(BlockPos pos) {
		return getDelegate().getBrightness(pos);
	}

	@Override
	public double getDismountHeight(BlockPos pos) {
		return getDelegate().getDismountHeight(pos);
	}

	@Override
	public <T extends Entity> List<T> getNonSpectatingEntities(
			Class<? extends T> entityClass, Box box) {
		return getDelegate().getNonSpectatingEntities(entityClass, box);
	}

	@Override
	public int getStrongRedstonePower(BlockPos pos, Direction direction) {
		return getDelegate().getStrongRedstonePower(pos, direction);
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop) {
		return false;
	}

	@Override
	public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(
			Class<? extends T> entityClass, Box box) {
		return getDelegate().getEntitiesIncludingUngeneratedChunks(entityClass, box);
	}

	@Override
	public Chunk getChunk(BlockPos pos) {
		return getDelegate().getChunk(pos);
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop,
			Entity breakingEntity) {
		return false;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus status) {
		return getDelegate().getChunk(chunkX, chunkZ, status);
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return getDelegate().spawnEntity(entity);
	}

	@Override
	public boolean equals(Object obj) {
		return getDelegate().equals(obj);
	}

	@Override
	public boolean isWater(BlockPos pos) {
		return getDelegate().isWater(pos);
	}

	@Override
	public boolean containsFluid(Box box) {
		return getDelegate().containsFluid(box);
	}

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public PlayerEntity getClosestPlayer(double x, double y, double z,
			double maxDistance, Predicate<Entity> targetPredicate) {
		return getDelegate().getClosestPlayer(x, y, z, maxDistance, targetPredicate);
	}

	@Override
	public MinecraftServer getServer() {
		return null;
	}

	@Override
	public int getLightLevel(BlockPos pos) {
		return getDelegate().getLightLevel(pos);
	}

	@Override
	public int getLightLevel(BlockPos pos, int ambientDarkness) {
		return getDelegate().getLightLevel(pos, ambientDarkness);
	}

	@Override
	public boolean isChunkLoaded(BlockPos pos) {
		return getDelegate().isChunkLoaded(pos);
	}

	@Override
	public boolean isRegionLoaded(BlockPos min, BlockPos max) {
		return getDelegate().isRegionLoaded(min, max);
	}

	@Override
	public boolean isRegionLoaded(int minX, int minY, int minZ, int maxX,
			int maxY, int maxZ) {
		return getDelegate().isRegionLoaded(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public WorldChunk getWorldChunk(BlockPos pos) {
		return getDelegate().getWorldChunk(pos);
	}

	@Override
	public PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
		return getDelegate().getClosestPlayer(entity, maxDistance);
	}

	@Override
	public WorldChunk getChunk(int i, int j) {
		return getDelegate().getChunk(i, j);
	}

	@Override
	public PlayerEntity getClosestPlayer(double x, double y, double z,
			double maxDistance, boolean ignoreCreative) {
		return getDelegate().getClosestPlayer(x, y, z, maxDistance, ignoreCreative);
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus,
			boolean create) {
		return getDelegate().getChunk(chunkX, chunkZ, leastStatus, create);
	}

	@Override
	public boolean isPlayerInRange(double x, double y, double z, double range) {
		return getDelegate().isPlayerInRange(x, y, z, range);
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		return false;
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state, int flags,
			int maxUpdateDepth) {
		return false;
	}

	@Override
	public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate,
			LivingEntity entity) {
		return getDelegate().getClosestPlayer(targetPredicate, entity);
	}

	@Override
	public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate,
			LivingEntity entity, double x, double y, double z) {
		return getDelegate().getClosestPlayer(targetPredicate, entity, x, y, z);
	}

	@Override
	public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate,
			double x, double y, double z) {
		return getDelegate().getClosestPlayer(targetPredicate, x, y, z);
	}

	@Override
	public <T extends LivingEntity> T getClosestEntity(
			Class<? extends T> entityClass, TargetPredicate targetPredicate,
			LivingEntity entity, double x, double y, double z, Box box) {
		return getDelegate().getClosestEntity(entityClass, targetPredicate, entity,
				x, y, z, box);
	}

	@Override
	public <T extends LivingEntity> T getClosestEntityIncludingUngeneratedChunks(
			Class<? extends T> entityClass, TargetPredicate targetPredicate,
			LivingEntity entity, double x, double y, double z, Box box) {
		return getDelegate().getClosestEntityIncludingUngeneratedChunks(entityClass,
				targetPredicate, entity, x, y, z, box);
	}

	@Override
	public <T extends LivingEntity> T getClosestEntity(
			List<? extends T> entityList, TargetPredicate targetPredicate,
			LivingEntity entity, double x, double y, double z) {
		return getDelegate().getClosestEntity(entityList, targetPredicate, entity, x,
				y, z);
	}

	@Override
	public void onBlockChanged(BlockPos pos, BlockState oldBlock,
			BlockState newBlock) {
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		return false;
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop, Entity breakingEntity,
			int maxUpdateDepth) {
		return false;
	}

	@Override
	public List<PlayerEntity> getPlayers(TargetPredicate targetPredicate,
			LivingEntity entity, Box box) {
		return getDelegate().getPlayers(targetPredicate, entity, box);
	}

	@Override
	public <T extends LivingEntity> List<T> getTargets(
			Class<? extends T> entityClass, TargetPredicate targetPredicate,
			LivingEntity targetingEntity, Box box) {
		return getDelegate().getTargets(entityClass, targetPredicate,
				targetingEntity, box);
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	public void updateListeners(BlockPos pos, BlockState oldState,
			BlockState newState, int flags) {
	}

	@Override
	public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old,
			BlockState updated) {
	}

	@Override
	public void updateNeighborsAlways(BlockPos pos, Block block) {
	}

	@Override
	public PlayerEntity getPlayerByUuid(UUID uuid) {
		return getDelegate().getPlayerByUuid(uuid);
	}

	@Override
	public void updateNeighborsExcept(BlockPos pos, Block sourceBlock,
			Direction direction) {
	}

	@Override
	public void updateNeighbor(BlockPos sourcePos, Block sourceBlock,
			BlockPos neighborPos) {
	}

	@Override
	public int getTopY(Type heightmap, int x, int z) {
		return getDelegate().getTopY(heightmap, x, z);
	}

	@Override
	public LightingProvider getLightingProvider() {
		return getDelegate().getLightingProvider();
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return getDelegate().getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return getDelegate().getFluidState(pos);
	}

	@Override
	public boolean isDay() {
		return getDelegate().isDay();
	}

	@Override
	public boolean isNight() {
		return getDelegate().isNight();
	}

	@Override
	public void playSound(PlayerEntity player, BlockPos pos, SoundEvent sound,
			SoundCategory category, float volume, float pitch) {
		getDelegate().playSound(player, pos, sound, category, volume, pitch);
	}

	@Override
	public void playSound(PlayerEntity player, double x, double y, double z,
			SoundEvent sound, SoundCategory category, float volume,
			float pitch) {
		getDelegate().playSound(player, x, y, z, sound, category, volume, pitch);
	}

	@Override
	public void playSoundFromEntity(PlayerEntity player, Entity entity,
			SoundEvent sound, SoundCategory category, float volume,
			float pitch) {
		getDelegate().playSoundFromEntity(player, entity, sound, category, volume,
				pitch);
	}

	@Override
	public void playSound(double x, double y, double z, SoundEvent sound,
			SoundCategory category, float volume, float pitch, boolean bl) {
		getDelegate().playSound(x, y, z, sound, category, volume, pitch, bl);
	}

	@Override
	public void addParticle(ParticleEffect parameters, double x, double y,
			double z, double velocityX, double velocityY, double velocityZ) {
		getDelegate().addParticle(parameters, x, y, z, velocityX, velocityY,
				velocityZ);
	}

	@Override
	public void addParticle(ParticleEffect parameters, boolean alwaysSpawn,
			double x, double y, double z, double velocityX, double velocityY,
			double velocityZ) {
		getDelegate().addParticle(parameters, alwaysSpawn, x, y, z, velocityX,
				velocityY, velocityZ);
	}

	@Override
	public void addImportantParticle(ParticleEffect parameters, double x,
			double y, double z, double velocityX, double velocityY,
			double velocityZ) {
		getDelegate().addImportantParticle(parameters, x, y, z, velocityX, velocityY,
				velocityZ);
	}

	@Override
	public void addImportantParticle(ParticleEffect parameters,
			boolean alwaysSpawn, double x, double y, double z, double velocityX,
			double velocityY, double velocityZ) {
		getDelegate().addImportantParticle(parameters, alwaysSpawn, x, y, z,
				velocityX, velocityY, velocityZ);
	}

	@Override
	public float getSkyAngleRadians(float tickDelta) {
		return getDelegate().getSkyAngleRadians(tickDelta);
	}

	@Override
	public boolean addBlockEntity(BlockEntity blockEntity) {
		return false;
	}

	@Override
	public void addBlockEntities(Collection<BlockEntity> blockEntities) {
	}

	@Override
	public void tickBlockEntities() {
	}

	@Override
	public void tickEntity(Consumer<Entity> tickConsumer, Entity entity) {
	}

	@Override
	public String getDebugString() {
		return getDelegate().getDebugString();
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return getDelegate().getBlockEntity(pos);
	}

	@Override
	public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
	}

	@Override
	public boolean canSetBlock(BlockPos pos) {
		return false;
	}

	@Override
	public boolean isDirectionSolid(BlockPos pos, Entity entity,
			Direction direction) {
		return getDelegate().isDirectionSolid(pos, entity, direction);
	}

	@Override
	public boolean isTopSolid(BlockPos pos, Entity entity) {
		return getDelegate().isTopSolid(pos, entity);
	}

	@Override
	public void calculateAmbientDarkness() {
		getDelegate().calculateAmbientDarkness();
	}

	@Override
	public void setMobSpawnOptions(boolean spawnMonsters,
			boolean spawnAnimals) {
		getDelegate().setMobSpawnOptions(spawnMonsters, spawnAnimals);
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public BlockView getExistingChunk(int chunkX, int chunkZ) {
		return getDelegate().getExistingChunk(chunkX, chunkZ);
	}

	@Override
	public List<Entity> getOtherEntities(Entity except, Box box,
			Predicate<? super Entity> predicate) {
		return getDelegate().getOtherEntities(except, box, predicate);
	}

	@Override
	public <T extends Entity> List<T> getEntitiesByType(EntityType<T> type,
			Box box, Predicate<? super T> predicate) {
		return getDelegate().getEntitiesByType(type, box, predicate);
	}

	@Override
	public <T extends Entity> List<T> getEntitiesByClass(
			Class<? extends T> entityClass, Box box,
			Predicate<? super T> predicate) {
		return getDelegate().getEntitiesByClass(entityClass, box, predicate);
	}

	@Override
	public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(
			Class<? extends T> entityClass, Box box,
			Predicate<? super T> predicate) {
		return getDelegate().getEntitiesIncludingUngeneratedChunks(entityClass, box,
				predicate);
	}

	@Override
	public Entity getEntityById(int id) {
		return getDelegate().getEntityById(id);
	}

	@Override
	public void markDirty(BlockPos pos, BlockEntity blockEntity) {
	}

	@Override
	public int getSeaLevel() {
		return getDelegate().getSeaLevel();
	}

	@Override
	public int getReceivedStrongRedstonePower(BlockPos pos) {
		return getDelegate().getReceivedStrongRedstonePower(pos);
	}

	@Override
	public boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
		return getDelegate().isEmittingRedstonePower(pos, direction);
	}

	@Override
	public int getEmittedRedstonePower(BlockPos pos, Direction direction) {
		return getDelegate().getEmittedRedstonePower(pos, direction);
	}

	@Override
	public boolean isReceivingRedstonePower(BlockPos pos) {
		return getDelegate().isReceivingRedstonePower(pos);
	}

	@Override
	public int getReceivedRedstonePower(BlockPos pos) {
		return getDelegate().getReceivedRedstonePower(pos);
	}

	@Override
	public void disconnect() {
	}

	@Override
	public long getTime() {
		return getDelegate().getTime();
	}

	@Override
	public long getTimeOfDay() {
		return getDelegate().getTimeOfDay();
	}

	@Override
	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return getDelegate().canPlayerModifyAt(player, pos);
	}

	@Override
	public void sendEntityStatus(Entity entity, byte status) {
		getDelegate().sendEntityStatus(entity, status);
	}

	@Override
	public void addSyncedBlockEvent(BlockPos pos, Block block, int type,
			int data) {
		getDelegate().addSyncedBlockEvent(pos, block, type, data);
	}

	@Override
	public WorldProperties getLevelProperties() {
		return getDelegate().getLevelProperties();
	}

	@Override
	public GameRules getGameRules() {
		return getDelegate().getGameRules();
	}

	@Override
	public float getThunderGradient(float delta) {
		return getDelegate().getThunderGradient(delta);
	}

	@Override
	public void setThunderGradient(float thunderGradient) {
		getDelegate().setThunderGradient(thunderGradient);
	}

	@Override
	public float getRainGradient(float delta) {
		return getDelegate().getRainGradient(delta);
	}

	@Override
	public void setRainGradient(float rainGradient) {
		getDelegate().setRainGradient(rainGradient);
	}

	@Override
	public boolean isThundering() {
		return getDelegate().isThundering();
	}

	@Override
	public boolean isRaining() {
		return getDelegate().isRaining();
	}

	@Override
	public boolean hasRain(BlockPos pos) {
		return getDelegate().hasRain(pos);
	}

	@Override
	public boolean hasHighHumidity(BlockPos pos) {
		return getDelegate().hasHighHumidity(pos);
	}

	@Override
	public MapState getMapState(String id) {
		return getDelegate().getMapState(id);
	}

	@Override
	public void putMapState(MapState mapState) {
		getDelegate().putMapState(mapState);
	}

	@Override
	public int getNextMapId() {
		return getDelegate().getNextMapId();
	}

	@Override
	public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
		getDelegate().syncGlobalEvent(eventId, pos, data);
	}

	@Override
	public CrashReportSection addDetailsToCrashReport(CrashReport report) {
		return getDelegate().addDetailsToCrashReport(report);
	}

	@Override
	public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
		getDelegate().setBlockBreakingInfo(entityId, pos, progress);
	}

	@Override
	public void addFireworkParticle(double x, double y, double z,
			double velocityX, double velocityY, double velocityZ,
			CompoundTag tag) {
		getDelegate().addFireworkParticle(x, y, z, velocityX, velocityY, velocityZ,
				tag);
	}

	@Override
	public void updateComparators(BlockPos pos, Block block) {
		getDelegate().updateComparators(pos, block);
	}

	@Override
	public LocalDifficulty getLocalDifficulty(BlockPos pos) {
		return getDelegate().getLocalDifficulty(pos);
	}

	@Override
	public int getAmbientDarkness() {
		return getDelegate().getAmbientDarkness();
	}

	@Override
	public void setLightningTicksLeft(int lightningTicksLeft) {
		getDelegate().setLightningTicksLeft(lightningTicksLeft);
	}

	@Override
	public WorldBorder getWorldBorder() {
		return getDelegate().getWorldBorder();
	}

	@Override
	public void sendPacket(Packet<?> packet) {
	}

	@Override
	public DimensionType getDimension() {
		return getDelegate().getDimension();
	}

	@Override
	public RegistryKey<World> getRegistryKey() {
		return getDelegate().getRegistryKey();
	}

	@Override
	public Random getRandom() {
		return getDelegate().getRandom();
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return getDelegate().testBlockState(pos, state);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return getDelegate().getRecipeManager();
	}

	@Override
	public TagManager getTagManager() {
		return getDelegate().getTagManager();
	}

	@Override
	public BlockPos getRandomPosInChunk(int x, int y, int z, int i) {
		return getDelegate().getRandomPosInChunk(x, y, z, i);
	}

	@Override
	public boolean isSavingDisabled() {
		return true;
	}

}

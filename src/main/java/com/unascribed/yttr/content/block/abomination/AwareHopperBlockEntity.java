package com.unascribed.yttr.content.block.abomination;

import java.util.List;
import java.util.Optional;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;
import com.unascribed.yttr.util.DelegatingInventory;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class AwareHopperBlockEntity extends AbstractAbominationBlockEntity implements Tickable, SidedInventory, DelegatingInventory {
	
	private Identifier recipe;
	private CraftingInventory input = new CraftingInventory(new ScreenHandler(ScreenHandlerType.CRAFTING, -1) {
		@Override
		public boolean canUse(PlayerEntity player) {
			return false;
		}
	}, 3, 3);
	private SimpleInventory remainder = new SimpleInventory(9);
	private SimpleInventory eject = new SimpleInventory(9);
	private SimpleInventory output = new SimpleInventory(1);
	
	// :)
	private DoubleInventory union = new DoubleInventory(input, new DoubleInventory(new DoubleInventory(remainder, eject), output)) {
		@Override
		public void setStack(int slot, ItemStack stack) {
			super.setStack(slot, stack);
			AwareHopperBlockEntity.this.markDirty();
		}
		@Override
		public ItemStack removeStack(int slot) {
			ItemStack rtrn = super.removeStack(slot);
			AwareHopperBlockEntity.this.markDirty();
			return rtrn;
		}
		@Override
		public void clear() {
			super.clear();
			AwareHopperBlockEntity.this.markDirty();
		}
		@Override
		public ItemStack removeStack(int slot, int amount) {
			ItemStack rtrn = super.removeStack(slot, amount);
			AwareHopperBlockEntity.this.markDirty();
			return rtrn;
		}
	};
	
	public int craftingTicks = 0;
	public int transferCooldown = 0;
	
	public AwareHopperBlockEntity() {
		super(YBlockEntities.AWARE_HOPPER);
	}

	@Override
	public void tick() {
		super.tick();
		boolean blind = isBlind();
		if (!blind) {
			Vec3d head = getHeadPos();
			PlayerEntity player = world.getClosestPlayer(head.x, head.y, head.z, 4, false);
			if (player != null) {
				Vec3d delta = player.getCameraPosVec(1).subtract(head);
				this.headYaw = (float) Math.toDegrees(MathHelper.atan2(delta.z, delta.x));
				this.headPitch = (float) Math.toDegrees(MathHelper.atan2(-delta.y, Math.sqrt(delta.x*delta.x + delta.z*delta.z)));
				markDirty();
			} else {
				if (headPitch > 40) {
					headPitch = Math.max(headPitch-5, 40);
					markDirty();
				} else if (headPitch < 40) {
					headPitch = Math.min(headPitch+5, 40);
					markDirty();
				}
			}
		}
		if (!world.isClient) {
			boolean suffocating = isSuffocating();
			CraftingRecipe r = getRecipe();
			if (!suffocating && r != null && r.matches(input, world) && output.isEmpty() && remainder.isEmpty()) {
				if (craftingTicks == 0) {
					world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 0, 1);
				}
				craftingTicks++;
				if (craftingTicks >= 100) {
					ItemStack outputStack = r.craft(input);
					DefaultedList<ItemStack> remainders = world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, input, world);
					for (int i = 0; i < input.size(); i++) {
						input.removeStack(i, 1);
						if (i < remainders.size()) {
							ItemStack rem = remainders.get(i);
							int index = remapInvIndexToRecipeIndex(r, i);
							Inventory tgt = remainder;
							if (input.getStack(i).isEmpty()) {
								if (r instanceof SpecialInputsRecipe) {
									if (((SpecialInputsRecipe)r).yttr$isInputValid(input, index, rem)) {
										tgt = input;
									}
								} else if (r.getPreviewInputs().size() > index && r.getPreviewInputs().get(index).test(rem)) {
									tgt = input;
								}
							}
							tgt.setStack(i, rem);
						}
					}
					if (world instanceof ServerWorld) {
						((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, r.getOutput()),
								pos.getX()+0.5, pos.getY()+0.75, pos.getZ()+0.5, 20,
								0.2, 0.1, 0.2, 0.05);
					}
					output.setStack(0, outputStack);
					world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 0, 0);
					craftingTicks = -20;
				} else if (craftingTicks > 5) {
					if (world instanceof ServerWorld) {
						ItemStack in = input.getStack(world.random.nextInt(input.size()));
						if (!in.isEmpty()) {
							((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, in),
									pos.getX()+0.5, pos.getY()+0.6, pos.getZ()+0.5, 1,
									0.15, 0.1, 0.15, 0.05);
						}
					}
				}
				markDirty();
			} else if (craftingTicks > 0) {
				if (craftingTicks != 0) {
					world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 0, 0);
					markDirty();
				}
				craftingTicks = 0;
			}
			if (transferCooldown > 0) {
				markDirty();
			}
			transferCooldown--;
			if (!output.isEmpty() && transferCooldown <= 0) {
				Direction facing = getCachedState().get(AwareHopperBlock.FACING);
				BlockEntity be = world.getBlockEntity(pos.offset(facing));
				if (be instanceof Inventory) {
					ItemStack is = output.getStack(0);
					ItemStack copy = is.copy();
					copy.setCount(1);
					if (HopperBlockEntity.transfer(output, (Inventory)be, copy, facing.getOpposite()).isEmpty()) {
						is.decrement(1);
						output.setStack(0, is);
					}
					transferCooldown = 8;
				}
			}
		} else if (world.random.nextInt(5) == 0) {
			world.addParticle(ParticleTypes.PORTAL,
					pos.getX() + 0.5, pos.getY() + 1.75, pos.getZ() + 0.5, (world.random.nextDouble() - 0.5) * 2, -world.random.nextDouble(), (world.random.nextDouble() - 0.5) * 2);
		}
	}
	
	@Override
	public boolean canSay() {
		return !isBlind();
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return YSounds.AWARE_HOPPER_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound() {
		return YSounds.AWARE_HOPPER_BREAK;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		if (recipe != null) tag.putString("Recipe", recipe.toString());
		tag.put("Inventory", Yttr.serializeInv(union));
		tag.putInt("CraftingTicks", craftingTicks);
		tag.putInt("TransferCooldown", transferCooldown);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		recipe = tag.contains("Recipe", NbtType.STRING) ? Identifier.tryParse(tag.getString("Recipe")) : null;
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), union);
		craftingTicks = tag.getInt("CraftingTicks");
		transferCooldown = tag.getInt("TransferCooldown");
	}
	
	public void drop() {
		ItemScatterer.spawn(world, pos, union);
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag tag = super.toInitialChunkDataTag();
		tag.putInt("CraftingTicks", craftingTicks);
		return tag;
	}
	
	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (!world.isClient) return false;
		if (type == 0) {
			craftingTicks = data;
			return true;
		}
		return false;
	}

	public boolean isBlind() {
		BlockState bs = world.getBlockState(pos);
		return bs.getBlock() == YBlocks.AWARE_HOPPER && bs.get(AwareHopperBlock.BLIND);
	}

	public void onNearbyCrafting(PlayerEntity player, CraftingInventory learnInput) {
		if (!isBlind()) {
			if (player.squaredDistanceTo(getHeadPos()) < 4*4) {
				Vec3d head = getHeadPos();
				if (world.raycast(new RaycastContext(head, player.getCameraPosVec(1), ShapeType.COLLIDER, FluidHandling.NONE, player)).getType() == Type.MISS) {
					Optional<CraftingRecipe> recipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, learnInput, world);
					if (recipe.isPresent()) {
						if (recipe.get().getOutput().getItem() == YBlocks.AWARE_HOPPER.asItem()) {
							sayTicks = -60;
							world.playSound(null, pos, YSounds.AWARE_HOPPER_SCREAM, SoundCategory.BLOCKS, 1, (world.random.nextFloat()-world.random.nextFloat())*0.2f + 1);
							return;
						}
						Identifier id = recipe.get().getId();
						if (!Objects.equal(id, this.recipe)) {
							if (!input.isEmpty()) {
								if (eject.isEmpty()) {
									for (int i = 0; i < input.size(); i++) {
										eject.setStack(i, input.removeStack(i));
									}
								} else {
									if (world instanceof ServerWorld) {
										((ServerWorld)world).spawnParticles(ParticleTypes.SMOKE, head.x, head.y, head.z, 8, 0.2, 0.2, 0.2, 0);
									}
									return;
								}
							}
							if (world instanceof ServerWorld) {
								((ServerWorld)world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, head.x, head.y, head.z, 8, 0.2, 0.2, 0.2, 0);
							}
							this.recipe = id;
						}
					}
				}
			}
		}
	}

	@Override
	public Vec3d getHeadPos() {
		return new Vec3d(pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5);
	}

	@Override
	public Inventory getDelegateInv() {
		return union;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			// nothing
			return new int[0];
		}
		if (side == getCachedState().get(AwareHopperBlock.FACING)) {
			// just output
			return new int[] { 27 };
		} else if (side == getCachedState().get(AwareHopperBlock.FACING).getOpposite()) {
			// just input
			return new int[] {
					0, 1, 2,
					3, 4, 5,
					6, 7, 8
			};
		}
		// input and aux output
		return new int[] {
			 0,  1,  2,
			 3,  4,  5,
			 6,  7,  8,
			
			 9, 10, 11,
			12, 13, 14,
			15, 16, 17,
			
			18, 19, 20,
			21, 22, 23,
			24, 25, 26
		};
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (slot >= 9) return false;
		CraftingRecipe recipe = getRecipe();
		if (recipe == null) return false;
		DefaultedList<Ingredient> inputs = recipe.getPreviewInputs();
		List<Integer> candidates = Lists.newArrayList();
		for (int i = 0; i < (recipe instanceof SpecialInputsRecipe ? 9 : inputs.size()); i++) {
			if (recipe instanceof SpecialInputsRecipe) {
				if (((SpecialInputsRecipe)recipe).yttr$isInputValid(input, i, stack)) {
					candidates.add(i);
				}
			} else if (inputs.get(i).test(stack)) {
				candidates.add(i);
			}
		}
		int smallestStack = -1;
		int smallestStackCount = Integer.MAX_VALUE;
		for (int i : candidates) {
			int index = remapRecipeIndexToInvIndex(recipe, i);
			int c = input.getStack(index).getCount();
			if (c < smallestStackCount) {
				smallestStack = index;
				smallestStackCount = c;
			}
		}
		return slot == smallestStack;
	}

	private int remapRecipeIndexToInvIndex(CraftingRecipe recipe, int i) {
		if (recipe instanceof ShapedRecipe) {
			ShapedRecipe sr = (ShapedRecipe)recipe;
			int x = i%sr.getWidth();
			int y = i/sr.getWidth();
			return (y*3)+x;
		}
		return i;
	}
	
	private int remapInvIndexToRecipeIndex(CraftingRecipe recipe, int i) {
		if (recipe instanceof ShapedRecipe) {
			ShapedRecipe sr = (ShapedRecipe)recipe;
			int x = i%3;
			int y = i/3;
			return (y*sr.getWidth())+x;
		}
		return i;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot >= 9;
	}
	
	private CraftingRecipe getRecipe() {
		if (recipe == null) return null;
		Recipe<?> r = world.getRecipeManager().get(recipe).orElse(null);
		if (!(r instanceof CraftingRecipe)) return null;
		return (CraftingRecipe)r;
	}

	
}

package com.unascribed.yttr.block;

import java.util.Random;

import com.unascribed.yttr.block.entity.CentrifugeBlockEntity;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.inventory.CentrifugeScreenHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CentrifugeBlock extends Block implements BlockEntityProvider {

	public static final BooleanProperty LIT = Properties.LIT;
	
	public CentrifugeBlock(Settings settings) {
		super(FabricBlockSettings.copyOf(settings)
				.emissiveLighting((state, view, pos) -> state.get(LIT))
				.luminance((state) -> state.get(LIT) ? 8 : 0));
		setDefaultState(getDefaultState().with(LIT, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LIT);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (state.get(LIT)) {
			double x = pos.getX() + 0.5;
			double y = pos.getY();
			double z = pos.getZ() + 0.5;
			if (rand.nextInt(8) == 0) {
				world.playSound(x, y, z, YSounds.CENTRIFUGE, SoundCategory.BLOCKS, 0.5f+(rand.nextFloat()/2), 1+((rand.nextFloat()-rand.nextFloat())*0.4f), false);
			}
			if (rand.nextInt(10) == 0) {
				world.playSound(x, y, z, YSounds.CENTRIFUGE_CRACKLE, SoundCategory.BLOCKS, 1, 1, false);
			}

			for (Direction d : Direction.Type.HORIZONTAL) {
				Direction.Axis axis = d.getAxis();
				double depthOfs = 0.52;
				double horzOfs = rand.nextDouble() * 0.5 - 0.25;
				double pX = axis == Direction.Axis.X ? d.getOffsetX() * depthOfs : horzOfs;
				double pY = (rand.nextDouble() + 2) / 16;
				double pZ = axis == Direction.Axis.Z ? d.getOffsetZ() * depthOfs : horzOfs;
				world.addParticle(ParticleTypes.SMOKE, x + pX, y + pY, z + pZ, 0, 0, 0);
				world.addParticle(ParticleTypes.FLAME, x + pX, y + pY, z + pZ, 0, 0, 0);
			}
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new CentrifugeBlockEntity();
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CentrifugeBlockEntity) {
			player.openHandledScreen(new NamedScreenHandlerFactory() {
				
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new CentrifugeScreenHandler((CentrifugeBlockEntity)be, syncId, inv, ((CentrifugeBlockEntity)be).getProperties());
				}
				
				@Override
				public Text getDisplayName() {
					return ((CentrifugeBlockEntity)be).getName();
				}
			});
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof CentrifugeBlockEntity) {
				ItemScatterer.spawn(world, pos, (CentrifugeBlockEntity)be);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

}

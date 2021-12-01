package com.unascribed.yttr.content.item;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedLogic;
import com.unascribed.yttr.inred.MultimeterProbeProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class InRedMultimeterItem extends Item {
	public InRedMultimeterItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		Block block = world.getBlockState(pos).getBlock();
		PlayerEntity player = ctx.getPlayer();
		String value;
		Text message;
		if (world.isClient) return ActionResult.PASS;
		if (block instanceof MultimeterProbeProvider) {
			// Great! There's a provider for the Block here.
			message = ((MultimeterProbeProvider)block).getProbeMessage(world, pos, world.getBlockState(pos));
		} else if (block == YBlocks.INRED_CABLE || block == YBlocks.INRED_SCAFFOLD) {
			// One of our wires. Take a search in all directions and return the signal being passed through.
			value = getWireValue(world, pos);
			message = new TranslatableText("tip.yttr.inred.multimeter.cable", value);
//        } else if (block == ModBlocks.DEVICE_LIQUID_CRYSTAL) {
			// Liquid Crystal is not currently implemented, hopefully it'll be fixed sometime!
//            value = getValue(world, pos, facing);
//            message = new TranslatableText("tip.yttr.inred.multimeter.direction", ctx.getPlayerFacing().getName(), value);
		} else if (InRedLogic.checkCandidacy(world, pos, ctx.getPlayerFacing())) {
			// Someone else's InRed-compat block, but it doesn't have a provider. Check using a general getValue.
			value = getValue(world, pos, ctx.getPlayerFacing());
			message = new TranslatableText("tip.yttr.inred.multimeter.direction", ctx.getPlayerFacing().getName(), value);
		} else {
			// Not something the Multimeter can detect. Nothing to send.
			return ActionResult.PASS;
		}
		// show up in the status bar!
		if (message != null) player.sendMessage(message, true);
		return ActionResult.SUCCESS;
	}

	private String getValue(World world, BlockPos pos, Direction face) {
		int signal = InRedLogic.findIRValue(world, pos, face.getOpposite());
		int bit1 = ((signal & 0b00_0001) != 0) ? 1:0;
		int bit2 = ((signal & 0b00_0010) != 0) ? 1:0;
		int bit3 = ((signal & 0b00_0100) != 0) ? 1:0;
		int bit4 = ((signal & 0b00_1000) != 0) ? 1:0;
		int bit5 = ((signal & 0b01_0000) != 0) ? 1:0;
		int bit6 = ((signal & 0b10_0000) != 0) ? 1:0;
		return ": 0b"+bit6+bit5+"_"+bit4+bit3+bit2+bit1+" ("+signal+")";
	}

	private String getWireValue(World world, BlockPos pos) {
		int signal = 0;
		for (Direction dir : Direction.values()) {
			signal |= InRedLogic.findIRValue(world, pos, dir);
		}
		int bit1 = ((signal & 0b00_0001) != 0) ? 1:0;
		int bit2 = ((signal & 0b00_0010) != 0) ? 1:0;
		int bit3 = ((signal & 0b00_0100) != 0) ? 1:0;
		int bit4 = ((signal & 0b00_1000) != 0) ? 1:0;
		int bit5 = ((signal & 0b01_0000) != 0) ? 1:0;
		int bit6 = ((signal & 0b10_0000) != 0) ? 1:0;
		return ": 0b"+bit6+bit5+"_"+bit4+bit3+bit2+bit1+" ("+signal+")";
	}
}

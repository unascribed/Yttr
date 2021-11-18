package com.unascribed.yttr.client.render.block_entity;

import static com.unascribed.yttr.content.block.abomination.SkeletalSorterBlockEntity.STOW_TIME;
import static com.unascribed.yttr.content.block.abomination.SkeletalSorterBlockEntity.THINK_TIME;

import com.unascribed.yttr.content.block.abomination.SkeletalSorterBlock;
import com.unascribed.yttr.content.block.abomination.SkeletalSorterBlockEntity;
import com.unascribed.yttr.util.math.Interp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class SkeletalSorterBlockEntityRenderer extends BlockEntityRenderer<SkeletalSorterBlockEntity> {

	private static final Identifier SKELETON_TEXTURE = new Identifier("minecraft", "textures/entity/skeleton/skeleton.png");
	private static final Identifier GOGGLES_TEXTURE = new Identifier("yttr", "textures/models/armor/goggles_layer_1.png");
	
	private final SkeletonEntityModel<SkeletonEntity> skeletonModel = new SkeletonEntityModel<>();
	
	public SkeletalSorterBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(SkeletalSorterBlockEntity entity, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		matrices.translate(0.5, 0.5, 0.5);
		float ang = 0;
		switch (entity.getCachedState().get(SkeletalSorterBlock.FACING)) {
			case NORTH:
				ang = 0;
				break;
			case EAST:
				ang = 270;
				break;
			case SOUTH:
				ang = 180;
				break;
			case WEST:
				ang = 90;
				break;
			default:
				ang = -70;
				break;
		}
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(ang));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
		matrices.translate(0, -0.5, 0);
		matrices.translate(0, 0, -1/16f);
		skeletonModel.child = false;
		
		skeletonModel.body.visible = true;
		skeletonModel.body.pivotX = -3;
		skeletonModel.body.pivotY = 13.9f;
		skeletonModel.body.pivotZ = -6;
		skeletonModel.body.pitch = (float)Math.PI/2;
		skeletonModel.body.yaw = (float)Math.PI/6;
		
		skeletonModel.leftLeg.visible = false;
		skeletonModel.rightLeg.visible = false;
		
		skeletonModel.rightArm.visible = true;
		skeletonModel.rightArm.pivotY = -1;
		skeletonModel.rightArm.pivotX = -6;
		skeletonModel.rightArm.pivotZ = -6;
		skeletonModel.rightArm.pitch = -(float)Math.PI/2;
		skeletonModel.rightArm.yaw = 0;
		
		skeletonModel.leftArm.visible = true;
		skeletonModel.leftArm.pivotY = -1;
		skeletonModel.leftArm.pivotX = 6;
		skeletonModel.leftArm.pivotZ = -6;
		skeletonModel.leftArm.pitch = -(float)Math.PI/2;
		skeletonModel.leftArm.yaw = 0;
		
		skeletonModel.head.yaw = 0;
		
		skeletonModel.leftArm.pitch += 0.5f;
		skeletonModel.rightArm.pitch += 0.5f;
		
		float t = (entity.age+delta)/10;
		
		Arm mainArm = entity.getCachedState().get(SkeletalSorterBlock.MAIN_HAND).arm;
		boolean leftHanded = mainArm == Arm.LEFT;
		float yawMul = leftHanded ? 1 : -1;
		ModelPart mainHand = leftHanded ? skeletonModel.leftArm : skeletonModel.rightArm;
		ModelPart offHand = leftHanded ? skeletonModel.rightArm : skeletonModel.leftArm;
		
		if (entity.thinkTicks > 0) {
			float thinkT = entity.thinkTicks+delta;
			if (thinkT > THINK_TIME) thinkT = THINK_TIME;
			float a = Interp.sCurve5(thinkT > 8 ? 1 : thinkT/8f);
			mainHand.pitch -= a;
			mainHand.yaw += (0.5f*yawMul)*a;
		} else if (entity.stowTicks > 0) {
			ModelPart hand = entity.stowing == Hand.MAIN_HAND ? mainHand : offHand;
			yawMul = entity.stowing == Hand.MAIN_HAND ? yawMul : yawMul*-1;
			float stowT = entity.stowTicks+delta;
			if (stowT > STOW_TIME) stowT = STOW_TIME;
			float a = MathHelper.sin((stowT/STOW_TIME)*((float)Math.PI));
			float yawDist = 2.5f;
			float pitchDist = 1;
			if (entity.stowing == Hand.MAIN_HAND) {
				hand.yaw += (0.5f*yawMul);
				yawDist = 3;
			}
			hand.yaw -= a*yawDist*yawMul;
			hand.pitch -= MathHelper.sin(a*((float)Math.PI))*pitchDist;
			hand.pivotX += a*2*yawMul;
			skeletonModel.head.yaw = -a*0.5f*yawMul;
		}
		
		skeletonModel.leftArm.yaw += 0.2f+MathHelper.sin(t)/40;
		skeletonModel.leftArm.pitch += MathHelper.cos(t)/40;
		
		skeletonModel.rightArm.yaw += -0.2f-MathHelper.sin(t)/40;
		skeletonModel.rightArm.pitch += -MathHelper.cos(t)/40;
		
		skeletonModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(SKELETON_TEXTURE)), light, overlay, 1, 1, 1, 1);
		
		if (entity.getCachedState().get(SkeletalSorterBlock.ENGOGGLED)) {
			matrices.push();
			matrices.scale(1.25f, 1.25f, 1.25f);
			matrices.translate(0, 1/16f, 0);
			skeletonModel.body.visible = false;
			skeletonModel.leftArm.visible = false;
			skeletonModel.rightArm.visible = false;
			skeletonModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(GOGGLES_TEXTURE)), light, overlay, 1, 1, 1, 1);
			matrices.pop();
		}
		
		MinecraftClient mc = MinecraftClient.getInstance();
		
		ItemStack left = leftHanded ? entity.heldItemMainHand : entity.heldItemOffHand;
		ItemStack right = leftHanded ? entity.heldItemOffHand : entity.heldItemMainHand;
		
		if (entity.stowTicks < STOW_TIME/2) {
			matrices.push();
			skeletonModel.leftArm.rotate(matrices);
			matrices.translate(0, 0.65, 0);
			matrices.scale(0.5f, 0.5f, 0.5f);
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-50));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(40));
			mc.getItemRenderer().renderItem(null, left, Mode.FIXED, false, matrices, vertexConsumers, null, light, overlay);
			matrices.pop();
			
			matrices.push();
			skeletonModel.rightArm.rotate(matrices);
			matrices.translate(0, 0.65, 0);
			matrices.scale(0.5f, 0.5f, 0.5f);
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-50));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-40));
			mc.getItemRenderer().renderItem(null, right, Mode.FIXED, false, matrices, vertexConsumers, null, light, overlay);
			matrices.pop();
		}
		
		matrices.pop();
	}
	
}

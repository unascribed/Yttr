// Made with Blockbench 3.8.3

package com.unascribed.yttr.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ZendermieModel extends EntityModel<Entity> {
	public final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart inside;
	
	public final ModelPart arms;
	private final ModelPart arm1;
	private final ModelPart arm2;
	
	public ZendermieModel() {
		textureWidth = 64;
		textureHeight = 32;
		head = new ModelPart(this);
		head.setPivot(0.0155F, 13.844F, 0.0301F);
		head.setTextureOffset(0, 0).addCuboid(-4.0155F, -5.844F, -4.0301F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		jaw = new ModelPart(this);
		jaw.setPivot(3.9845F, 0.656F, 3.4699F);
		head.addChild(jaw);
		setRotationAngle(jaw, 0.1745F, 0.0F, -0.0873F);
		jaw.setTextureOffset(0, 16).addCuboid(-7.9536F, -4.4681F, -7.4098F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		inside = new ModelPart(this);
		inside.setPivot(-0.0155F, 0.656F, -0.0301F);
		head.addChild(inside);
		setRotationAngle(inside, 3.1416F, 0.0F, 0.0F);
		inside.setTextureOffset(32, 16).addCuboid(-4.0F, -0.5F, -4.0F, 8.0F, 3.0F, 8.0F, -0.01F, false);

        arms = new ModelPart(this);
        arms.setPivot(5.0F, 27.0F, 7.0F);

        arm1 = new ModelPart(this);
        arm1.setPivot(-7.0F, 0.0F, -14.0F);
        arms.addChild(arm1);
        setRotationAngle(arm1, 1.2217F, -0.2618F, 0.2618F);
        arm1.setTextureOffset(48, 0).addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        arm2 = new ModelPart(this);
        arm2.setPivot(-2.0F, 0.0F, 0.0F);
        arms.addChild(arm2);
        setRotationAngle(arm2, -1.309F, 0.0873F, 0.2618F);
        arm2.setTextureOffset(48, 0).addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float t = (ageInTicks/20)%((float)Math.PI*2);
		jaw.roll = -0.0873f+MathHelper.sin(t)*0.05f;
		jaw.pitch = 0.1745f+MathHelper.cos(t)*0.045f;
		head.yaw = (float)Math.toRadians(netHeadYaw);
		head.pitch = (float)Math.toRadians(headPitch);
		
		boolean crafting = limbSwing > 0;
		float vigor = crafting ? 0.2f : 0.05f;
		float fervor = crafting ? 20 : 1;
		
		arm1.yaw = MathHelper.cos(t*fervor)*vigor;
		arm1.pitch = 1.3f-(MathHelper.sin(t*fervor)*vigor);
		
		arm2.yaw = MathHelper.sin(t*fervor)*vigor;
		arm2.pitch = -1.3f-(MathHelper.cos(t*fervor)*vigor);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		arms.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart bone, float x, float y, float z) {
		bone.pitch = x;
		bone.yaw = y;
		bone.roll = z;
	}

}
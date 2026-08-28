package crab.mods.crabsspelllbooks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class HasturSetModel extends HumanoidModel<LivingEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(CrabsSpellbooks.MODID, "hastur_armor"), "main");

	public HasturSetModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		// CubeDeformation applied to outer armor elements prevents Z-fighting against base body parts

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(24, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
				.texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.65F))
				.texOffs(0, 25).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.7F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("Head_r1", CubeListBuilder.create()
				.texOffs(0, 48).addBox(-4.0F, -0.75F, -0.42F, 8.0F, 3.0F, 5.0F, new CubeDeformation(0.6F)), PartPose.offsetAndRotation(0.0F, -7.0F, 4.0F, -0.7854F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create()
				.texOffs(24, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.8F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(32, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.35F))
				.texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 21.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(26, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.55F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("Body Layer_r1", CubeListBuilder.create()
				.texOffs(56, 32).addBox(-4.0F, -0.2F, -2.0F, 8.0F, 10.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, 0.1745F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(50, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.35F))
				.texOffs(32, 58).addBox(-3.0F, 7.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.45F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(0, 56).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.35F))
				.texOffs(16, 58).addBox(-1.0F, 7.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.45F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(56, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(56, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public void setSlotPartVisibility(EquipmentSlot slot) {
		this.setAllVisible(false);

		switch (slot) {
			case HEAD -> {
				this.head.visible = true;
				this.hat.visible = true;
			}
			case CHEST -> {
				this.body.visible = true;
				this.rightArm.visible = true;
				this.leftArm.visible = true;
			}
			case LEGS -> {
				this.body.visible = true;
				this.rightLeg.visible = true;
				this.leftLeg.visible = true;
			}
			case FEET -> {
				this.rightLeg.visible = true;
				this.leftLeg.visible = true;
			}
			default -> {}
		}
	}
}
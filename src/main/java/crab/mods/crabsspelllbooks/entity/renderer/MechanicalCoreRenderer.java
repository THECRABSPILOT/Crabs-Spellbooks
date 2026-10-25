package crab.mods.crabsspelllbooks.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import crab.mods.crabsspelllbooks.entity.MechanicalCoreEntity;
import crab.mods.crabsspelllbooks.entity.model.MechanicalCoreModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MechanicalCoreRenderer extends GeoEntityRenderer<MechanicalCoreEntity> {
    public MechanicalCoreRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MechanicalCoreModel());
        this.shadowRadius = 1.5f; // Scaled shadow (0.5f * 3)
    }

    @Override
    public void preRender(PoseStack poseStack, MechanicalCoreEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.scale(3.0f, 3.0f, 3.0f);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
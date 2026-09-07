package crab.mods.crabsspelllbooks.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import crab.mods.crabsspelllbooks.blocks.entity.RiftBlockEntity;
import crab.mods.crabsspelllbooks.blocks.models.RiftBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RiftBlockRenderer extends GeoBlockRenderer<RiftBlockEntity> {
    public RiftBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new RiftBlockModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, RiftBlockEntity animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.scale(5.0f, 5.0f, 5.0f);
        poseStack.translate(-0.5D, -0.3D, -0.5D);

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.popPose();
    }
}
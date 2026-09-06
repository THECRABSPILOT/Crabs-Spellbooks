package crab.mods.crabsspelllbooks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import crab.mods.crabsspelllbooks.blocks.entity.BeamBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class BeamBlockEntityRenderer implements BlockEntityRenderer<BeamBlockEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/misc/white.png");

    private static final int CYLINDER_SIDES = 32;

    public BeamBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            BeamBlockEntity be,
            float partialTick,
            PoseStack pose,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        if (!be.isMaster() || be.getLevel() == null) {
            return;
        }

        float radius = be.getBeamRadius();
        float height = BeamBlockEntity.BEAM_HEIGHT;

        Vec3 beamCenter = be.getBeamCenter();
        double centerX = beamCenter.x - be.getBlockPos().getX();
        double centerZ = beamCenter.z - be.getBlockPos().getZ();

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(TEXTURE));
        PoseStack.Pose lastPose = pose.last();

        double angleStep = (Math.PI * 2.0) / CYLINDER_SIDES;

        // Fetch dynamic RGB multiplier (defaults to 1.0, 1.0, 1.0 for White)
        float[] colors = be.getColorMultiplier();
        int r = (int) (colors[0] * 255.0f);
        int g = (int) (colors[1] * 255.0f);
        int b = (int) (colors[2] * 255.0f);
        int a = 210; // Transparency

        for (int i = 0; i < CYLINDER_SIDES; i++) {
            double a1 = i * angleStep;
            double a2 = (i + 1) * angleStep;

            float x1 = (float) (centerX + Math.cos(a1) * radius);
            float z1 = (float) (centerZ + Math.sin(a1) * radius);
            float x2 = (float) (centerX + Math.cos(a2) * radius);
            float z2 = (float) (centerZ + Math.sin(a2) * radius);

            float u1 = (float) i / CYLINDER_SIDES;
            float u2 = (float) (i + 1) / CYLINDER_SIDES;

            // Outer Shell
            addQuadVertex(consumer, lastPose, x1, 0.0f, z1, r, g, b, a, u1, 0.0f, packedOverlay);
            addQuadVertex(consumer, lastPose, x1, height, z1, r, g, b, a, u1, 1.0f, packedOverlay);
            addQuadVertex(consumer, lastPose, x2, height, z2, r, g, b, a, u2, 1.0f, packedOverlay);
            addQuadVertex(consumer, lastPose, x2, 0.0f, z2, r, g, b, a, u2, 0.0f, packedOverlay);

            // Inner Shell
            addQuadVertex(consumer, lastPose, x2, 0.0f, z2, r, g, b, a, u2, 0.0f, packedOverlay);
            addQuadVertex(consumer, lastPose, x2, height, z2, r, g, b, a, u2, 1.0f, packedOverlay);
            addQuadVertex(consumer, lastPose, x1, height, z1, r, g, b, a, u1, 1.0f, packedOverlay);
            addQuadVertex(consumer, lastPose, x1, 0.0f, z1, r, g, b, a, u1, 0.0f, packedOverlay);
        }
    }

    private void addQuadVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y, float z,
            int r, int g, int b, int a,
            float u, float v,
            int overlay
    ) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0.0f, 1.0f, 0.0f)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(BeamBlockEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
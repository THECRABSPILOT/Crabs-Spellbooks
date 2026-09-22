package crab.mods.crabsspelllbooks.items.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import crab.mods.crabsspelllbooks.items.MechanicalJetpackItem;
import crab.mods.crabsspelllbooks.items.model.MechanicalJetpackModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class MechanicalJetpackRenderer extends GeoArmorRenderer<MechanicalJetpackItem> implements ICurioRenderer {

    public MechanicalJetpackRenderer() {
        super(new MechanicalJetpackModel());
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        LivingEntity entity = slotContext.entity();

        if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel && stack.getItem() instanceof MechanicalJetpackItem jetpackItem) {
            this.prepForRender(entity, stack, EquipmentSlot.CHEST, humanoidModel);

            poseStack.pushPose();

            this.renderToBuffer(
                    poseStack,
                    null,
                    light,
                    getPackedOverlay(jetpackItem, 0.0F),
                    1.0F, 1.0F, 1.0F, 1.0F
            );

            poseStack.popPose();
        }
    }
}
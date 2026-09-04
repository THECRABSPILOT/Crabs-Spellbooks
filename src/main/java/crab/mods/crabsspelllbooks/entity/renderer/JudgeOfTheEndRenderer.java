package crab.mods.crabsspelllbooks.entity.renderer;

import crab.mods.crabsspelllbooks.entity.JudgeOfTheEndEntity;
import crab.mods.crabsspelllbooks.entity.model.JudgeOfTheEndModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class JudgeOfTheEndRenderer extends GeoEntityRenderer<JudgeOfTheEndEntity> {
    public JudgeOfTheEndRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new JudgeOfTheEndModel());
        this.shadowRadius = 0.5f;
    }
}

package crab.mods.crabsspelllbooks.events;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.blocks.RiftBlockRenderer;
import crab.mods.crabsspelllbooks.client.BeamBlockEntityRenderer;
import crab.mods.crabsspelllbooks.client.model.HasturSetModel;
import crab.mods.crabsspelllbooks.client.model.ModModelLayers;
import crab.mods.crabsspelllbooks.entity.renderer.JudgeOfTheEndRenderer;
import crab.mods.crabsspelllbooks.registry.BlockRegistry;
import crab.mods.crabsspelllbooks.registry.EntityRegistry;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CrabsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.HASTUR_ARMOR, HasturSetModel::createBodyLayer);
    }


    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        // This instructs Forge to actively listen for "loader": "forge:obj" in your JSONs
        event.register("obj", ObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation("crabs_spellbooks", "nova_arm_layer"),
                42, // Priority layer
                player -> new ModifierLayer<>()
        );
    }
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.JUDGE_OF_THE_END.get(), JudgeOfTheEndRenderer::new);
        event.registerBlockEntityRenderer(
                BlockRegistry.BEAM_BLOCK_ENTITY.get(),
                BeamBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(BlockRegistry.RIFT_BLOCK_ENTITY.get(), RiftBlockRenderer::new);
    }


}
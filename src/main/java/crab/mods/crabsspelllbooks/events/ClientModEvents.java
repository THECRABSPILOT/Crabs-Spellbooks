package crab.mods.crabsspelllbooks.events;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.blocks.RiftBlockRenderer;
import crab.mods.crabsspelllbooks.client.BeamBlockEntityRenderer;
import crab.mods.crabsspelllbooks.client.model.HasturSetModel;
import crab.mods.crabsspelllbooks.client.model.ModModelLayers;
import crab.mods.crabsspelllbooks.entity.mobs.judge.JudgeOfTheEndRenderer;
import crab.mods.crabsspelllbooks.entity.mobs.core.MechanicalCoreRenderer;
import crab.mods.crabsspelllbooks.items.renderer.MechanicalJetpackRenderer;
import crab.mods.crabsspelllbooks.registry.BlockRegistry;
import crab.mods.crabsspelllbooks.registry.EntityRegistry;
import crab.mods.crabsspelllbooks.registry.ItemRegistry;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = CrabsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    public static final ModelLayerLocation MECHPACK_LAYER = new ModelLayerLocation(
            new ResourceLocation("crabs_spellbooks", "mechpack"), "main"
    );

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.HASTUR_ARMOR, HasturSetModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("obj", ObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.JUDGE_OF_THE_END.get(), JudgeOfTheEndRenderer::new);
        event.registerBlockEntityRenderer(
                BlockRegistry.BEAM_BLOCK_ENTITY.get(),
                BeamBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(BlockRegistry.RIFT_BLOCK_ENTITY.get(), RiftBlockRenderer::new);
        event.registerEntityRenderer(EntityRegistry.MECHANICAL_CORE.get(), MechanicalCoreRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Player animation factory setup
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation("crabs_spellbooks", "nova_arm_layer"),
                42,
                player -> new ModifierLayer<>()
        );

        // Curios registry setup
        event.enqueueWork(() -> {
            CuriosRendererRegistry.register(ItemRegistry.MECHPACK.get(), () ->
                    new MechanicalJetpackRenderer()
            );
        });
    }
}
package crab.mods.crabsspelllbooks.events;

import crab.mods.crabsspelllbooks.blocks.RiftBlockRenderer;
import crab.mods.crabsspelllbooks.client.BeamBlockEntityRenderer;
import crab.mods.crabsspelllbooks.client.model.HasturSetModel;
import crab.mods.crabsspelllbooks.client.model.ModModelLayers;
import crab.mods.crabsspelllbooks.registry.BlockRegistry;
import crab.mods.crabsspelllbooks.registry.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "crabs_spellbooks", value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(ModEffects.SENSORY_DEPRIVATION.get())) {
            if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) ||
                    event.getOverlay().id().equals(VanillaGuiOverlay.ARMOR_LEVEL.id())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.HASTUR_ARMOR, HasturSetModel::createBodyLayer);

    }

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(ModEffects.SENSORY_DEPRIVATION.get())) {
            event.setSound(null);
        }
    }


}

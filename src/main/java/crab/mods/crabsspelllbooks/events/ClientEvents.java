package crab.mods.crabsspelllbooks.events;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.items.JudgeArmorItem;
import crab.mods.crabsspelllbooks.items.MechArmorItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import crab.mods.crabsspelllbooks.client.model.HasturSetModel;
import crab.mods.crabsspelllbooks.client.model.ModModelLayers;
import crab.mods.crabsspelllbooks.registry.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

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
    public static void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        // Check if the boss bar matches your custom boss name
        if (event.getBossEvent().getName().getString().equals("§5§1~ JUDGE OF THE END ~")) {
            event.setCanceled(true);


        } else if (event.getBossEvent().getName().getString().equals("§4 THE MECHANICAL CORE")) {
            event.setCanceled(true);


        }
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.HASTUR_ARMOR, HasturSetModel::createBodyLayer);

    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (event.getEntity() instanceof AbstractClientPlayer player) {
            PlayerModel<AbstractClientPlayer> playerModel = event.getRenderer().getModel();

            // Check equipment slots
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof JudgeArmorItem || player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof MechArmorItem) {
                playerModel.head.visible = false;
                playerModel.hat.visible = false;
            }

            if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof JudgeArmorItem || player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof MechArmorItem) {
                playerModel.body.visible = false;
                playerModel.rightArm.visible = false;
                playerModel.leftArm.visible = false;
                playerModel.jacket.visible = false;
                playerModel.rightSleeve.visible = false;
                playerModel.leftSleeve.visible = false;
            }

            if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof JudgeArmorItem || player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof MechArmorItem) {
                playerModel.rightPants.visible = true;
                playerModel.leftPants.visible = true;
            }

            if (player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof JudgeArmorItem || player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof MechArmorItem) {
                playerModel.rightLeg.visible = true;
                playerModel.leftLeg.visible = true;
            }
        }
    }


    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(ModEffects.SENSORY_DEPRIVATION.get())) {
            event.setSound(null);
        }
    }


}

package crab.mods.crabsspelllbooks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrabsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CustomBossBarRenderer {

    // Define your boss bar textures
    private static final ResourceLocation JUDGE_BAR =
            new ResourceLocation(CrabsSpellbooks.MODID, "textures/gui/bossbars/judge_bossbar.png");

    private static final ResourceLocation CORE_BAR =
            new ResourceLocation(CrabsSpellbooks.MODID, "textures/gui/bossbars/corebar.png");


    @SubscribeEvent
    public static void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        String bossName = event.getBossEvent().getName().getString();


        ResourceLocation texture = null;
        int barWidth = 182;
        int barHeight = 15;


        if (bossName.contains("JUDGE OF THE END")) {
            texture = JUDGE_BAR;
            barWidth = 182;
            barHeight = 15;
        } else if (bossName.contains("THE MECHANICAL CORE")) {
                texture = CORE_BAR;
            barWidth = 182;
            barHeight = 15;
        }

        if (texture != null) {

            event.setCanceled(true);

            GuiGraphics guiGraphics = event.getGuiGraphics();
            int x = event.getX();
            int y = event.getY();
            float progress = event.getBossEvent().getProgress();

            RenderSystem.setShaderTexture(0, texture);

            guiGraphics.blit(
                    texture,
                    x, y,
                    0, 0,
                    barWidth, barHeight,
                    barWidth, barHeight * 2
            );

            int progressWidth = (int) (barWidth * progress);
            if (progressWidth > 0) {
                guiGraphics.blit(
                        texture,
                        x, y,
                        0, barHeight,
                        progressWidth, barHeight,
                        barWidth, barHeight * 2
                );
            }

            Minecraft mc = Minecraft.getInstance();
            Component nameComponent = event.getBossEvent().getName();
            int textWidth = mc.font.width(nameComponent);
            int textX = x + (barWidth / 2) - (textWidth / 2);
            int textY = y - 9;

            guiGraphics.drawString(mc.font, nameComponent, textX, textY, 0xFFFFFFFF, true);

            event.setIncrement(barHeight + 12);
        }
    }
}
package crab.mods.crabsspelllbooks.client;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.network.JetpackInputPacket;
import crab.mods.crabsspelllbooks.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrabsSpellbooks.MODID, value = Dist.CLIENT)
public class ClientInputHandler {
    private static boolean lastJumpState = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            boolean jumpPressed = mc.options.keyJump.isDown();

            // Send packet every tick if jump is held, or once when released
            if (jumpPressed || jumpPressed != lastJumpState) {
                lastJumpState = jumpPressed;
                PacketHandler.sendToServer(new JetpackInputPacket(jumpPressed));
            }
        }
    }
}
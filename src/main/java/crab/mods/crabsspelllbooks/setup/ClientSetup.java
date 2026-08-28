package crab.mods.crabsspelllbooks.setup;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.registry.ItemRegistry;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = CrabsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosRendererRegistry.register(
                    ItemRegistry.ELECTRICIAN_MANUAL.get(),
                    SpellBookCurioRenderer::new
            );
            CuriosRendererRegistry.register(
                    ItemRegistry.ELDRITCH_GRIMOIRE.get(),
                    SpellBookCurioRenderer::new
            );
            CuriosRendererRegistry.register(
                    ItemRegistry.DICTIONARY.get(),
                    SpellBookCurioRenderer::new
            );
            CuriosRendererRegistry.register(
                    ItemRegistry.TOME_OF_TEMPEST.get(),
                    SpellBookCurioRenderer::new
            );

        });
    }
}
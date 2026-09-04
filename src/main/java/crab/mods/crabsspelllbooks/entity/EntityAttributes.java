package crab.mods.crabsspelllbooks.entity;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.registry.EntityRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = CrabsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributes {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.JUDGE_OF_THE_END.get(),
                Monster.createMonsterAttributes()
                        .add(Attributes.MAX_HEALTH, 100.0D)
                        .add(Attributes.MOVEMENT_SPEED, 0.25D)
                        .add(Attributes.ATTACK_DAMAGE, 8.0D)
                        .build()
        );
    }
}

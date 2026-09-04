package crab.mods.crabsspelllbooks.registry;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.entity.JudgeOfTheEndEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {

    // Fixed DeferredRegister generic type to EntityType<?>
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CrabsSpellbooks.MODID);

    public static final RegistryObject<EntityType<JudgeOfTheEndEntity>> JUDGE_OF_THE_END =
            ENTITY_TYPES.register("judge_of_the_end", () ->
                    EntityType.Builder.of(JudgeOfTheEndEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.9f)
                            .clientTrackingRange(8)
                            .build("judge_of_the_end")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
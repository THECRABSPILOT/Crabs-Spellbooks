package crab.mods.crabsspelllbooks.events;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.registry.ItemRegistry; // Replace with your Item registry class
import crab.mods.crabsspelllbooks.registry.ModEffects;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = CrabsSpellbooks.MODID)
public class HasturSetAbility {

    private static final UUID SPELL_POWER_BUFF_UUID = UUID.fromString("c97b8a12-8924-4f0e-b81b-771192fa1234");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Run check once per tick on the logical server side
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player wearer = event.player;

        // Verify the wearer has the full set equipped
        if (!isWearingFullHasturSet(wearer)) {
            removeSpellPowerBuff(wearer);
            return;
        }

        boolean viewerFound = false;
        double radius = 16.0D; // Range within which entities looking at wearer get affected
        AABB searchArea = wearer.getBoundingBox().inflate(radius);
        List<LivingEntity> nearbyEntities = wearer.level().getEntitiesOfClass(LivingEntity.class, searchArea, e -> e != wearer && e.isAlive());

        for (LivingEntity viewer : nearbyEntities) {
            if (isLookingAtWearerHead(viewer, wearer)) {
                viewerFound = true;

                // Apply Sensory Deprivation (Iron's Spells) and Blindness (Vanilla) for 5 seconds (100 ticks)
                viewer.addEffect(new MobEffectInstance(ModEffects.SENSORY_DEPRIVATION.get(), 100, 0, false, true, true));
                viewer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, true, true));
            }
        }

        // Grant the wearer Strength and +10% Spell Power while at least one victim is afflicted
        if (viewerFound) {
            wearer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 0, false, false, true));
            applySpellPowerBuff(wearer);
        } else {
            removeSpellPowerBuff(wearer);
        }
    }

    /**
     * Checks if the viewer's gaze vector intersects with the wearer's head area.
     */
    private static boolean isLookingAtWearerHead(LivingEntity viewer, LivingEntity wearer) {
        Vec3 viewerEyePos = viewer.getEyePosition();
        Vec3 wearerHeadPos = wearer.getEyePosition();
        Vec3 lookVec = viewer.getViewVector(1.0F).normalize();

        Vec3 toWearerVec = wearerHeadPos.subtract(viewerEyePos);
        double distance = toWearerVec.length();

        if (distance > 24.0D) return false; // Hard cutoff distance

        toWearerVec = toWearerVec.normalize();

        // Dot product of 1.0 means looking directly at the head. 0.92 gives ~23 degree FOV tolerance.
        double dot = lookVec.dot(toWearerVec);
        if (dot > 0.92D) {
            // Ensure clear line of sight (no wall blocking view)
            return viewer.hasLineOfSight(wearer);
        }

        return false;
    }

    /**
     * Helper to verify if all armor slots match your Hastur armor items.
     */
    private static boolean isWearingFullHasturSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ItemRegistry.HASTUR_HELMET.get() &&
                player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ItemRegistry.HASTUR_CHESTPLATE.get() &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ItemRegistry.HASTUR_LEGGINGS.get() &&
                player.getItemBySlot(EquipmentSlot.FEET).getItem() == ItemRegistry.HASTUR_BOOTS.get();
    }

    private static void applySpellPowerBuff(Player wearer) {
        AttributeInstance attr = wearer.getAttribute(AttributeRegistry.SPELL_POWER.get());
        if (attr != null && attr.getModifier(SPELL_POWER_BUFF_UUID) == null) {
            attr.addTransientModifier(new AttributeModifier(
                    SPELL_POWER_BUFF_UUID,
                    "Hastur Full Set Gaze Buff",
                    0.10D, // +10% Spell Power
                    AttributeModifier.Operation.MULTIPLY_BASE
            ));
        }
    }

    private static void removeSpellPowerBuff(Player wearer) {
        AttributeInstance attr = wearer.getAttribute(AttributeRegistry.SPELL_POWER.get());
        if (attr != null && attr.getModifier(SPELL_POWER_BUFF_UUID) != null) {
            attr.removeModifier(SPELL_POWER_BUFF_UUID);
        }
    }
}
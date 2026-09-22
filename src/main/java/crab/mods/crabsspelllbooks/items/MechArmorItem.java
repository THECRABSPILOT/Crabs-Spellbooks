package crab.mods.crabsspelllbooks.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import crab.mods.crabsspelllbooks.items.renderer.MechArmorRender;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class MechArmorItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3E311-2576-4350-A0F4-2B8898F61706")
    };

    private Multimap<Attribute, AttributeModifier> attributeModifiers;

    public MechArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == this.getEquipmentSlot()) {
            if (this.attributeModifiers == null) {
                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                builder.putAll(super.getAttributeModifiers(slot, stack));

                UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];

                builder.put(AttributeRegistry.MAX_MANA.get(), new AttributeModifier(uuid, "Max Mana", 525.0D, AttributeModifier.Operation.ADDITION));
                builder.put(AttributeRegistry.LIGHTNING_SPELL_POWER.get(), new AttributeModifier(uuid, "Lightning Spell Power", 0.30D, AttributeModifier.Operation.MULTIPLY_BASE));
                builder.put(AttributeRegistry.SPELL_POWER.get(), new AttributeModifier(uuid, "Spell Power", 0.10D, AttributeModifier.Operation.MULTIPLY_BASE));

                this.attributeModifiers = builder.build();
            }
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private MechArmorRender renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new MechArmorRender();
                }

                // Prepares and returns the GeckoLib model for the current entity and slot
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    // GeckoLib Animation Controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Add controllers here if your armor has idle/walking animations
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
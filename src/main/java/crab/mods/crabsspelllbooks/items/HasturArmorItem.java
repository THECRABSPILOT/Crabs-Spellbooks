package crab.mods.crabsspelllbooks.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.client.model.HasturSetModel;
import crab.mods.crabsspelllbooks.client.model.ModModelLayers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class HasturArmorItem extends ArmorItem {

    private static final ResourceLocation HASTUR_TEXTURE =
            new ResourceLocation(CrabsSpellbooks.MODID, "textures/armor/hastur.png");

    // Unique UUIDs for each equipment slot to prevent attribute modifier stack collisions
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), // FEET
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), // LEGS
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), // CHEST
            UUID.fromString("2AD3E311-2576-4350-A0F4-2B8898F61706")  // HEAD
    };

    private Multimap<Attribute, AttributeModifier> attributeModifiers;

    public HasturArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        // Build custom modifiers only when equipped in the matching slot for this armor piece
        if (slot == this.getEquipmentSlot()) {
            if (this.attributeModifiers == null) {
                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();


                builder.putAll(super.getAttributeModifiers(slot, stack));

                UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];

                // +50 Max Mana
                builder.put(
                        AttributeRegistry.MAX_MANA.get(),
                        new AttributeModifier(uuid, "Max Mana", 50.0D, AttributeModifier.Operation.ADDITION)
                );

                builder.put(
                        AttributeRegistry.ELDRITCH_SPELL_POWER.get(),
                        new AttributeModifier(uuid, "Eldritch Spell Power", 0.10D, AttributeModifier.Operation.MULTIPLY_BASE)
                );

                this.attributeModifiers = builder.build();
            }
            return this.attributeModifiers;
        }

        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HasturSetModel cachedModel;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (this.cachedModel == null) {
                    this.cachedModel = new HasturSetModel(
                            Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.HASTUR_ARMOR)
                    );
                }

                defaultModel.copyPropertiesTo((HumanoidModel) this.cachedModel);
                this.cachedModel.setSlotPartVisibility(slot);

                return this.cachedModel;
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return HASTUR_TEXTURE.toString();
    }
}
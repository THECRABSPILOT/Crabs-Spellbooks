package crab.mods.crabsspelllbooks.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import crab.mods.crabsspelllbooks.items.model.AbyssalNovaModel;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.CastingItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class AbyssalNova extends CastingItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final UUID ELDRITCH_UUID = UUID.fromString("f4e6b18d-4f12-4a88-a28a-7b3400d3a901");


    public AbyssalNova() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .durability(1000)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create(super.getDefaultAttributeModifiers(slot));

        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            modifiers.put(
                    AttributeRegistry.ENDER_SPELL_POWER.get(),
                    new AttributeModifier(
                            ELDRITCH_UUID,
                            "Ender Spell Power",
                            0.20D,
                            AttributeModifier.Operation.MULTIPLY_BASE
                    )
            );
        }

        return modifiers;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<AbyssalNova> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    // Instantiate a standard GeoItemRenderer passing your AbyssalNovaModel
                    this.renderer = new GeoItemRenderer<>(new AbyssalNovaModel());
                }
                return this.renderer;
            }
        });
    }
}
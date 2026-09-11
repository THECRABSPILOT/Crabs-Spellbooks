package crab.mods.crabsspelllbooks.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import crab.mods.crabsspelllbooks.items.renderer.TheNovaRenderer;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.item.CastingItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class TheNova extends CastingItem implements GeoItem {
    private static final UUID ENDER_SPELL_POWER_UUID = UUID.fromString("c4e6b18d-4f12-4a88-a28a-7b3400d3a901");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TheNova() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .durability(800)
        );
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create(super.getDefaultAttributeModifiers(slot));

        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            modifiers.put(
                    AttributeRegistry.ENDER_SPELL_POWER.get(),
                    new AttributeModifier(
                            ENDER_SPELL_POWER_UUID,
                            "Ender Spell Power",
                            0.20D,
                            AttributeModifier.Operation.MULTIPLY_BASE
                    )
            );
        }

        return modifiers;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    public static final HumanoidModel.ArmPose ZOMBIE_ARM_POSE = HumanoidModel.ArmPose.create(
            "ZOMBIE_NOVA",
            false,
            (model, entity, arm) -> {
                if (arm == HumanoidArm.RIGHT) {
                    model.rightArm.xRot = (float) Math.toRadians(0);
                    model.rightArm.yRot = (float) Math.toRadians(0);;
                    model.rightArm.zRot = 0.0F;

                    model.rightArm.y -= 50.0F;
                } else {
                    model.leftArm.xRot = (float) Math.toRadians(10);
                    model.leftArm.yRot = (float) Math.toRadians(-45);;;
                    model.leftArm.zRot = (float) Math.toRadians(-20);;;
                }
            }
    );

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // Modern Forge / GeckoLib 4 setup
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private TheNovaRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new TheNovaRenderer();
                }
                return this.renderer;
            }

            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack itemStack) {
                if (!itemStack.isEmpty()) {
                    return ZOMBIE_ARM_POSE;
                }
                return HumanoidModel.ArmPose.EMPTY;
            }
        });
    }
}
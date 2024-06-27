package com.cleannrooster.cleannarsenal.Items;

import com.cleannrooster.cleannarsenal.client.item.renderer.RifleRenderer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.extraspellattributes.ReabsorptionInit.*;

public class Rifle extends BowItem implements GeoItem, ConfigurableAttributes {
    public Rifle(Settings settings) {
        super(settings);
    }
    private Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    private AnimatableInstanceCache factory = AzureLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private RifleRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (renderer == null) return new RifleRenderer();
                return this.renderer;
            }
        });
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if(slot== EquipmentSlot.MAINHAND ){
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            // builder.putAll(super.getAttributeModifiers(this.slot));
            builder.putAll(this.getAttributeModifiers(slot));
            builder.put(SpellSchools.FIRE.attribute,new EntityAttributeModifier(UUID.fromString("e529a6fc-63e3-4eab-88c2-121f58be9e36"),"fire",3, EntityAttributeModifier.Operation.ADDITION));
            builder.put(EntityAttributes_RangedWeapon.DAMAGE.attribute,new EntityAttributeModifier(UUID.fromString("35d0c121-a434-4022-8020-cc929a0fb4a1"),"fire",3, EntityAttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getAttributeModifiers(stack, slot);
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }


    public void setAttributes(Multimap<EntityAttribute, EntityAttributeModifier> attributes) {
    }

}

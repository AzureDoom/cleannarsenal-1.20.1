package com.cleannrooster.cleannarsenal.Items;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.spell_engine.api.item.ConfigurableAttributes;

public class LaserBow extends BowItem implements ConfigurableAttributes {
    public Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    public LaserBow( Item.Settings properties) {
        super(properties);
    }
    public void setAttributes(Multimap<EntityAttribute, EntityAttributeModifier> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (this.attributes == null) {
            return super.getAttributeModifiers(slot);
        } else {
            return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getAttributeModifiers(slot);
        }
    }

}
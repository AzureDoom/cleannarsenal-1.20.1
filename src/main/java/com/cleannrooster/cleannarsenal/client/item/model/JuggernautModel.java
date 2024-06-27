package com.cleannrooster.cleannarsenal.client.item.model;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Armors.JuggernautArmor;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.spell_power.api.SpellSchools;

public class JuggernautModel extends GeoModel<JuggernautArmor> {

    public Identifier getModelResource(JuggernautArmor animatable) {

        return new Identifier(Cleannarsenal.MODID,"geo/juggmodel.geo.json");
    }

    @Override
    public Identifier getTextureResource(JuggernautArmor animatable) {

        return new Identifier(Cleannarsenal.MODID,"textures/armor/juggtexture.png");
    }

    @Override
    public Identifier getAnimationResource(JuggernautArmor animatable) {
        return null;
    }
}

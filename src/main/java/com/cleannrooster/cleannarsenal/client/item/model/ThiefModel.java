package com.cleannrooster.cleannarsenal.client.item.model;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Armors.JuggernautArmor;
import com.cleannrooster.cleannarsenal.Items.Armors.ThiefArmor;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;

public class ThiefModel extends GeoModel<ThiefArmor> {

    public Identifier getModelResource(ThiefArmor animatable) {

        return new Identifier(Cleannarsenal.MODID,"geo/thiefmodel.geo.json");
    }

    @Override
    public Identifier getTextureResource(ThiefArmor animatable) {

        return new Identifier(Cleannarsenal.MODID,"textures/armor/thieftexture.png");
    }

    @Override
    public Identifier getAnimationResource(ThiefArmor animatable) {
        return null;
    }
}

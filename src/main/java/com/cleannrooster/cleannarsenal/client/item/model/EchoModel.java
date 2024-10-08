package com.cleannrooster.cleannarsenal.client.item.model;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Armors.EchoArmor;
import com.cleannrooster.cleannarsenal.Items.Armors.JuggernautArmor;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;

public class EchoModel extends GeoModel<EchoArmor> {

    public Identifier getModelResource(EchoArmor animatable) {

        return new Identifier(Cleannarsenal.MODID,"geo/juggmodel.geo.json");
    }

    @Override
    public Identifier getTextureResource(EchoArmor animatable) {

        return new Identifier(Cleannarsenal.MODID,"textures/armor/juggtexture.png");
    }

    @Override
    public Identifier getAnimationResource(EchoArmor animatable) {
        return null;
    }
}

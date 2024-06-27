package com.cleannrooster.cleannarsenal.client.item.model;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Rifle;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;

public class RifleModel extends GeoModel<Rifle> {


    @Override
    public Identifier getModelResource(Rifle orb) {
        return new Identifier(Cleannarsenal.MODID   ,"geo/rifle.geo.json");
    }

    @Override
    public Identifier getTextureResource(Rifle orb) {

        return new Identifier(Cleannarsenal.MODID, "textures/item/rifle.png");
    }

    @Override
    public Identifier getAnimationResource(Rifle orb) {
        return null;
    }
}
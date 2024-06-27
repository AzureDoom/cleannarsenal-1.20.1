package com.cleannrooster.cleannarsenal.client.item.renderer;

import com.cleannrooster.cleannarsenal.Items.Rifle;
import com.cleannrooster.cleannarsenal.client.item.model.RifleModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class RifleRenderer extends GeoItemRenderer<Rifle> {


    public RifleRenderer() {
        super(new RifleModel());

    }
}

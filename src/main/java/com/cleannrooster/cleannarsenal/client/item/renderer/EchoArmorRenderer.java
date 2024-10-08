package com.cleannrooster.cleannarsenal.client.item.renderer;

import com.cleannrooster.cleannarsenal.Items.Armors.EchoArmor;
import com.cleannrooster.cleannarsenal.Items.Armors.JuggernautArmor;
import com.cleannrooster.cleannarsenal.client.item.model.EchoModel;
import com.cleannrooster.cleannarsenal.client.item.model.JuggernautModel;
import mod.azure.azurelib.renderer.GeoArmorRenderer;

public class EchoArmorRenderer extends GeoArmorRenderer<EchoArmor> {

    public EchoArmorRenderer() {
        super(new EchoModel());

    }
}

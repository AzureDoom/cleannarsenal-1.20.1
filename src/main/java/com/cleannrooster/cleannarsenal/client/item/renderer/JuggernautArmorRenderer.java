package com.cleannrooster.cleannarsenal.client.item.renderer;

import com.cleannrooster.cleannarsenal.Items.Armors.JuggernautArmor;
import com.cleannrooster.cleannarsenal.client.item.model.JuggernautModel;
import mod.azure.azurelib.renderer.GeoArmorRenderer;

public class JuggernautArmorRenderer extends GeoArmorRenderer<JuggernautArmor> {

    public JuggernautArmorRenderer() {
        super(new JuggernautModel());

    }
}

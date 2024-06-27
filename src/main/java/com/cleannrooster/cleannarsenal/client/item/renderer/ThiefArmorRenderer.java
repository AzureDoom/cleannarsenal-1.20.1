package com.cleannrooster.cleannarsenal.client.item.renderer;

import com.cleannrooster.cleannarsenal.Items.Armors.JuggernautArmor;
import com.cleannrooster.cleannarsenal.Items.Armors.ThiefArmor;
import com.cleannrooster.cleannarsenal.client.item.model.JuggernautModel;
import com.cleannrooster.cleannarsenal.client.item.model.ThiefModel;
import mod.azure.azurelib.renderer.GeoArmorRenderer;

public class ThiefArmorRenderer extends GeoArmorRenderer<ThiefArmor> {

    public ThiefArmorRenderer() {
        super(new ThiefModel());

    }
}

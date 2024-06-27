package com.cleannrooster.cleannarsenal.Items;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

public class Ram extends AxeItem {

    public Ram(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(ToolMaterials.NETHERITE, 9, -3.2F, settings);
    }
}

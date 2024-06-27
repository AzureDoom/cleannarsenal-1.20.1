package com.cleannrooster.cleannarsenal.Items;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

public class MissileWand extends AxeItem {

    public MissileWand(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(ToolMaterials.NETHERITE, 0, -3.2F, settings);
    }
}

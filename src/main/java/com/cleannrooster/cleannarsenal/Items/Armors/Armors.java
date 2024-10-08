package com.cleannrooster.cleannarsenal.Items.Armors;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Items;
import com.extraspellattributes.ReabsorptionInit;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Armors {
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();

    private static Armor.Entry create(Armor.CustomMaterial material, ItemConfig.ArmorSet defaults) {
        return new Armor.Entry(material, null, defaults);
    }

    public static final Armor.Set juggernaut =
            create(
                    new Armor.CustomMaterial(
                            "juggernaut",
                            50,
                            10,
                            SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                            () -> Ingredient.ofItems(Items.HEAVY.item, net.minecraft.item.Items.NETHERITE_SCRAP)
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.bonus(new Identifier("extraspellattributes", "defiance"), 0.5F),
                                            ItemConfig.Attribute.bonus(new Identifier( "minecraft","generic.armor_toughness"), 2F)
                                    )),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.bonus(new Identifier("extraspellattributes", "defiance"), 0.5F),
                                            ItemConfig.Attribute.bonus(new Identifier("minecraft", "generic.armor_toughness"), 2F)
                                    )),
                            new ItemConfig.ArmorSet.Piece(5)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.bonus(new Identifier("extraspellattributes", "defiance"), 0.5F),
                                            ItemConfig.Attribute.bonus(new Identifier( "minecraft","generic.armor_toughness"), 2F)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.bonus(new Identifier("extraspellattributes", "defiance"), 0.5F),
                                            ItemConfig.Attribute.bonus(new Identifier( "minecraft","generic.armor_toughness"), 2F)

                                            ))
                    )).bundle(material -> new Armor.Set(Cleannarsenal.MODID,
                            new JuggernautArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                            new JuggernautArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                            new JuggernautArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                            new JuggernautArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
                    ))
                    .put(entries).armorSet();
    ;

    public static final Armor.Set trickster =
            create(
                    new Armor.CustomMaterial(
                            "trickster",
                            35,
                            10,
                            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                            () -> Ingredient.ofItems(net.minecraft.item.Items.LEATHER)
                    ),

                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "spellsuppression"), 0.1F),
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "glancingblow"), 0.1F)
                                    )),
                            new ItemConfig.ArmorSet.Piece(5)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "spellsuppression"), 0.1F),
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "glancingblow"), 0.1F)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "spellsuppression"), 0.1F),
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "glancingblow"), 0.1F)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "spellsuppression"), 0.1F),
                                            ItemConfig.Attribute.multiply(new Identifier("extraspellattributes", "glancingblow"), 0.1F)

                                    ))
                    )).bundle(material -> new Armor.Set(Cleannarsenal.MODID,
                            new ThiefArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                            new ThiefArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                            new ThiefArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                            new ThiefArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
                    ))
                    .put(entries).armorSet();
    ;
    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries, Items.KEY);

    }

}

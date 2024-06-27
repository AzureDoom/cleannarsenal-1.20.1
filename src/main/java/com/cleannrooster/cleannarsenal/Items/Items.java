package com.cleannrooster.cleannarsenal.Items;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.spells.Spells;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.Function;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.AttributeResolver;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_engine.api.spell.CustomSpellHandler;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.UUID;

public class Items {
    public static ItemGroup ARSENAL;

    public static final ArrayList<Entry> entries = new ArrayList<>();
    private static Entry entry(String namespace, String name , Item item) {
        var entry = new Entry(namespace, name, item);
        entries.add(entry);

        return entry;
    }
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(),new Identifier(Cleannarsenal.MODID,"generic"));

    public static class Entry {
        public final String namespace;
        public final String name;

        public final Item item;

        public Entry(String namespace, String name,Item item) {
            this.namespace = namespace;
            this.name = name;
            this.item = item;
        }
    };

    public static Entry RIFLE = entry(Cleannarsenal.MODID,"rifle",new Rifle(new FabricItemSettings().fireproof().maxCount(1).maxDamage(2000)));
    public static Entry LASERBOW = entry(Cleannarsenal.MODID,"hailstorm",new LaserBow(new FabricItemSettings().fireproof().maxCount(1).maxDamage(2000)));
    public static Entry MISSILEWAND = entry(Cleannarsenal.MODID,"wand_of_missiles",
            new Weapon.Entry(
                    Cleannarsenal.MODID,
                    "wand_of_missiles",
                    Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND,
                            () -> Ingredient.ofItems(Items.STRANGE.item)),
                    new StaffItem(ToolMaterials.DIAMOND,new FabricItemSettings()),
                    new ItemConfig.Weapon(0,-3),null)
                    .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 3))
                    .item());

    public static Entry MISSILEWANDNETHERITE = entry(Cleannarsenal.MODID,"wand_of_missiles_netherite",
            new Weapon.Entry(
                    Cleannarsenal.MODID,
                    "wand_of_missiles",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE,
                            () -> Ingredient.ofItems(Items.STRANGE.item)),
                    new StaffItem(ToolMaterials.NETHERITE,new FabricItemSettings()),
                    new ItemConfig.Weapon(0,-3),null)
                    .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 4))
                    .item());


    public static Entry BURNT = entry(Cleannarsenal.MODID,"something_burnt",new Item(new FabricItemSettings().fireproof()));
    public static Entry FROZEN = entry(Cleannarsenal.MODID,"something_frozen",new Item(new FabricItemSettings()));
    public static Entry STRANGE = entry(Cleannarsenal.MODID,"something_strange",new Item(new FabricItemSettings()));
    public static Entry LASERARROWITEM = entry(Cleannarsenal.MODID,"laser_arrow",new Item(new FabricItemSettings()));
    public static Entry BATTERINGRAM = entry(Cleannarsenal.MODID,"batteringram",new Ram(ToolMaterials.NETHERITE,9,-3.2F, new FabricItemSettings().maxCount(1)));

    public static Entry HEAVY = entry(Cleannarsenal.MODID,"something_heavy",new Item(new FabricItemSettings()));

    public static void initializeItems(){
            ARSENAL = FabricItemGroup.builder()
                    .icon(() -> new ItemStack(RIFLE.item))
                    .displayName(Text.translatable("itemGroup.cleannarsenal.general"))
                    .build();
            Registry.register(Registries.ITEM_GROUP, KEY, ARSENAL);

        for(Entry entry : entries){
            Registry.register(Registries.ITEM,new Identifier(entry.namespace,entry.name),entry.item);

            ItemGroupEvents.modifyEntriesEvent(KEY).register((content) -> {
                content.add(entry.item);
            });
        }
        ((ConfigurableAttributes)MISSILEWAND.item)
                .setAttributes(attributesFrom(new ItemConfig.Weapon(0,-3)
                        .add(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 3))));
        ((ConfigurableAttributes)MISSILEWANDNETHERITE.item)
                .setAttributes(attributesFrom(new ItemConfig.Weapon(0,-3)
                        .add(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, 4))));
        ((ConfigurableAttributes)LASERBOW.item)
                .setAttributes(attributesFrom(new ItemConfig.Weapon(0,-3)
                        .add(ItemConfig.Attribute.bonus(SpellSchools.FROST.id, 2))
                        .add(ItemConfig.Attribute.bonus(EntityAttributes_RangedWeapon.DAMAGE.id,2))));


    }
    private static final UUID miscWeaponAttributeUUID = UUID.fromString("c102cb57-a7b8-4a98-8c6e-2cd7b70b74c1");
    private static final Identifier attackDamageId = new Identifier("generic.attack_damage");
    private static final Identifier projectileDamageId = new Identifier("projectile_damage", "generic");
    private static abstract class ItemAccessor extends Item {
        public ItemAccessor(Settings settings) { super(settings); }
        public static UUID ATTACK_DAMAGE_MODIFIER_ID() { return ATTACK_DAMAGE_MODIFIER_ID; }
        public static UUID ATTACK_SPEED_MODIFIER_ID() { return ATTACK_SPEED_MODIFIER_ID; }
    }
    private static Multimap<EntityAttribute, EntityAttributeModifier> attributesFrom(ItemConfig.Weapon config) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                        ItemAccessor.ATTACK_DAMAGE_MODIFIER_ID(),
                        "Weapon modifier",
                        config.attack_damage,
                        EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                        ItemAccessor.ATTACK_SPEED_MODIFIER_ID(),
                        "Weapon modifier",
                        config.attack_speed,
                        EntityAttributeModifier.Operation.ADDITION));
        for(var attribute: config.attributes) {
            if (attribute.value == 0) {
                continue;
            }
            try {
                var attributeId = new Identifier(attribute.id);
                var entityAttribute = AttributeResolver.get(attributeId);
                var uuid = (attributeId.equals(attackDamageId) || attributeId.equals(projectileDamageId))
                        ? ItemAccessor.ATTACK_DAMAGE_MODIFIER_ID()
                        : miscWeaponAttributeUUID;
                builder.put(entityAttribute,
                        new EntityAttributeModifier(
                                uuid,
                                "Weapon modifier",
                                attribute.value,
                                attribute.operation));
            } catch (Exception e) {
                System.err.println("Failed to add item attribute modifier: " + e.getMessage());
            }
        }
        return builder.build();
    }

}

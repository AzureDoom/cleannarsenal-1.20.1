package com.cleannrooster.cleannarsenal;

import com.cleannrooster.cleannarsenal.Config.Default;
import com.cleannrooster.cleannarsenal.Items.Armors.Armors;
import com.cleannrooster.cleannarsenal.Items.Items;
import com.cleannrooster.cleannarsenal.entities.LaserArrow;
import com.cleannrooster.cleannarsenal.spells.CustomStatusEffect;
import com.cleannrooster.cleannarsenal.spells.ShadeWalk;
import com.cleannrooster.cleannarsenal.spells.Spells;
import com.extraspellattributes.ReabsorptionInit;
import com.extraspellattributes.items.ItemInit;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.paladins.PaladinsMod;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.trinket.SpellBooks;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import net.spell_power.api.enchantment.Enchantments_SpellPower;
import net.tinyconfig.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.registry.Registries.ENTITY_TYPE;
import static net.minecraft.registry.Registries.STATUS_EFFECT;

public class Cleannarsenal implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("cleannarsenal");
	public static  String MODID = "cleannarsenal";
	public static SpellSchool RANGED_FIRE = SpellSchools.register(SpellSchools.createMagic("ranged_fire",11776947));
	public static SpellSchool RANGED_FROST = SpellSchools.register(SpellSchools.createMagic("ranged_frost",11776947));

	public static SpellSchool RANGED_FIRE_TIMING = SpellSchools.register(SpellSchools.createMagic("ranged_fire_timing",11776947));

	public static final Identifier RISE_ID = new Identifier(MODID,"rise");
	public static SoundEvent RISE_EVENT = SoundEvent.of(RISE_ID);
	public static EntityType<LaserArrow> LASERARROW;
	public static StatusEffect VALIANT;
	public static StatusEffect SHADEWALK;
	public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<ItemConfig>
			("items_v1", Default.itemConfig)
			.builder()
			.setDirectory(MODID)
			.sanitize(true)
			.build();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		itemConfig.refresh();
		Registry.register(Registries.SOUND_EVENT,RISE_ID,RISE_EVENT);
		VALIANT = new CustomStatusEffect(StatusEffectCategory.BENEFICIAL,16762624).addAttributeModifier(ReabsorptionInit.RECOUP,"5aa22ef4-e558-46e9-afea-fa0b25ffd3f6",0.08, EntityAttributeModifier.Operation.MULTIPLY_BASE);
		SHADEWALK = new ShadeWalk(StatusEffectCategory.BENEFICIAL,16762624);
		LASERARROW = Registry.register(
				ENTITY_TYPE,
				new Identifier(MODID, "laserarrow"),
				FabricEntityTypeBuilder.<LaserArrow>create(SpawnGroup.MISC, LaserArrow::new)
						.dimensions(EntityDimensions.fixed(0.5F, 0.5F)) // dimensions in Minecraft units of the render
						.trackRangeBlocks(128)
						.trackedUpdateRate(1)
						.build()
		);
		Registry.register(STATUS_EFFECT,new Identifier(MODID,"valiant"),VALIANT);
		Registry.register(STATUS_EFFECT,new Identifier(MODID,"shadewalk"),SHADEWALK);

		RANGED_FROST.attributeManagement = SpellSchool.Manage.INTERNAL;
		RANGED_FROST.addSource(SpellSchool.Trait.POWER, SpellSchool.Apply.ADD, queryArgs -> {
			var enchantment = Enchantments_SpellPower.SPELL_POWER;
			var level = EnchantmentHelper.getLevel(enchantment, queryArgs.entity().getMainHandStack());

			var enchantment3 = Enchantments.POWER;
			var level3 = EnchantmentHelper.getLevel(enchantment3, queryArgs.entity().getMainHandStack());
			if(level3 > 0){
				level3++;
			}
			var power = (double)0.75*SpellPower.getSpellPower(SpellSchools.FROST,queryArgs.entity()).nonCriticalValue()+0.75*SpellPower.getSpellPower(ExternalSpellSchools.PHYSICAL_RANGED,queryArgs.entity()).nonCriticalValue();
			power *=  1+(0.05 * level)+(0.25*level3);
			return power;
		});
		CustomModels.registerModelIds(List.of(
				new Identifier(MODID, "projectile/magicmissile")
		));
		SpellSchools.register(RANGED_FROST);
		RANGED_FIRE.attributeManagement = SpellSchool.Manage.INTERNAL;
		RANGED_FIRE.addSource(SpellSchool.Trait.POWER, SpellSchool.Apply.ADD, queryArgs -> {
			var enchantment = Enchantments_SpellPower.SPELL_POWER;
			var level = EnchantmentHelper.getLevel(enchantment, queryArgs.entity().getMainHandStack());

			var enchantment3 = Enchantments.POWER;
			var level3 = EnchantmentHelper.getLevel(enchantment3, queryArgs.entity().getMainHandStack());
			if(level3 > 0){
				level3++;
			}

			var power = (double)0.75*SpellPower.getSpellPower(SpellSchools.FIRE,queryArgs.entity()).nonCriticalValue()+0.75*SpellPower.getSpellPower(ExternalSpellSchools.PHYSICAL_RANGED,queryArgs.entity()).nonCriticalValue();
			power *=  1+(0.05 * level)+(0.25*level3);
			return power;
		});
		SpellSchools.register(RANGED_FIRE);
		RANGED_FIRE_TIMING.attributeManagement = SpellSchool.Manage.INTERNAL;
		RANGED_FIRE_TIMING.addSource(SpellSchool.Trait.POWER, SpellSchool.Apply.ADD, queryArgs -> {
			double modifier = 1;
			if(queryArgs.entity() instanceof PlayerInterface playerInterface){
				modifier = playerInterface.getFMJModifier();
			}
			return (double)SpellPower.getSpellPower(RANGED_FIRE,queryArgs.entity()).nonCriticalValue()*modifier;
		});
		SpellSchools.register(RANGED_FIRE_TIMING);
		Spells.initializeSpells();
		Items.initializeItems();
		Armors.register(itemConfig.value.armor_sets);

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
					if (source.isBuiltin() && LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
						LootPool.Builder poolBuilder = LootPool.builder()
								.with(ItemEntry.builder(Items.LASERBOW.item));
						poolBuilder.rolls(BinomialLootNumberProvider.create(1, 0.2F));
						tableBuilder.pool(poolBuilder);
						LootPool.Builder poolBuilder2 = LootPool.builder()
								.with(ItemEntry.builder(Items.RIFLE.item));

						poolBuilder2.rolls(BinomialLootNumberProvider.create(1, 0.2F));
						tableBuilder.pool(poolBuilder2);
						LootPool.Builder poolBuilder3 = LootPool.builder()
								.with(ItemEntry.builder(Items.BATTERINGRAM.item));

						poolBuilder3.rolls(BinomialLootNumberProvider.create(1, 0.2F));
						tableBuilder.pool(poolBuilder3);
					}

					if (source.isBuiltin() && LootTables.BASTION_TREASURE_CHEST.equals(id)) {
						LootPool.Builder poolBuilder = LootPool.builder()
								.with(ItemEntry.builder(Items.BURNT.item));
						poolBuilder.rolls(BinomialLootNumberProvider.create(1, 0.2F));
						tableBuilder.pool(poolBuilder);
						LootPool.Builder poolBuilder2 = LootPool.builder()
								.with(ItemEntry.builder(Items.STRANGE.item));

						poolBuilder2.rolls(BinomialLootNumberProvider.create(1, 0.2F));
						tableBuilder.pool(poolBuilder2);
						LootPool.Builder poolBuilder3 = LootPool.builder()
								.with(ItemEntry.builder(Items.HEAVY.item));

						poolBuilder3.rolls(BinomialLootNumberProvider.create(1, 0.2F));
						tableBuilder.pool(poolBuilder3);
						LootPool.Builder poolBuilder4 = LootPool.builder()
								.with(ItemEntry.builder(Items.FROZEN.item));

						poolBuilder4.rolls(BinomialLootNumberProvider.create(1, 0.2F));
						tableBuilder.pool(poolBuilder4);
					}
			if (source.isBuiltin() && LootTables.SIMPLE_DUNGEON_CHEST.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.builder()
						.with(ItemEntry.builder(Items.BURNT.item));
				poolBuilder.rolls(BinomialLootNumberProvider.create(1, 0.2F));
				tableBuilder.pool(poolBuilder);
				LootPool.Builder poolBuilder2 = LootPool.builder()
						.with(ItemEntry.builder(Items.STRANGE.item));

				poolBuilder2.rolls(BinomialLootNumberProvider.create(1, 0.2F));
				tableBuilder.pool(poolBuilder2);
				LootPool.Builder poolBuilder3 = LootPool.builder()
						.with(ItemEntry.builder(Items.HEAVY.item));

				poolBuilder3.rolls(BinomialLootNumberProvider.create(1, 0.2F));
				tableBuilder.pool(poolBuilder3);
				LootPool.Builder poolBuilder4 = LootPool.builder()
						.with(ItemEntry.builder(Items.FROZEN.item));

				poolBuilder4.rolls(BinomialLootNumberProvider.create(1, 0.2F));
				tableBuilder.pool(poolBuilder4);
			}
				}
		);

		SpellBooks.createAndRegister(new Identifier(MODID,"juggernaut"),Items.KEY);
		SpellBooks.createAndRegister(new Identifier(MODID,"trickster"),Items.KEY);

		LOGGER.info("Hello Fabric world!");
	}
}
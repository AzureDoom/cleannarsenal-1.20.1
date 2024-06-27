package com.cleannrooster.cleannarsenal.Config;

import com.cleannrooster.cleannarsenal.Items.Armors.Armors;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.loot.LootConfig;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Default {
    public final static ItemConfig itemConfig;
    public final static LootConfig lootConfig;
    static {
        itemConfig = new ItemConfig();

        for (var armor : Armors.entries) {
            itemConfig.armor_sets.put(armor.name(), armor.defaults());
        }


        lootConfig = new LootConfig();

        final var weapons = "weapons";

    }

    @SafeVarargs
    private static <T> List<T> joinLists(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}

package dev.lemonnik.moss.item;

import dev.lemonnik.moss.Moomoss;
import dev.lemonnik.moss.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item MOOMOSS_SPAWN_EGG = registerItem("moomoss_spawn_egg",
            new SpawnEggItem(ModEntities.MOOMOSS, 0x70922D, 0x50692C,
                    new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Moomoss.MOD_ID, name), item);
    }

    public static void addItemsToItemGroup() {
        addToItemGroup(ItemGroups.SPAWN_EGGS, MOOMOSS_SPAWN_EGG);
    }

    private static void addToItemGroup(RegistryKey<ItemGroup> group, Item item) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }
    public static void registerModItems() {
        Moomoss.LOGGER.info("Registering Mod Items for " + Moomoss.MOD_ID);

        addItemsToItemGroup();
    }
}

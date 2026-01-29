package net.vulpixass.soulspire.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.vulpixass.soulspire.Soulspire;
import net.vulpixass.soulspire.item.custom.SoulJamItem;
import net.vulpixass.soulspire.item.custom.SoulTotemItem;

public class ModItems {
    public static final Item SOUL_FRAGMENT = register("soul_fragment", s -> new Item(s));
    public static final Item SOUL_SHARD = register("soul_shard", s -> new Item(s.maxCount(16)));
    public static final Item SOUL_AMULET = register("soul_amulet", s -> new Item(s.maxCount(1)));
    public static final Item SOUL_CATALYST = register("soul_catalyst", s -> new Item(s.maxCount(1)));
    public static final Item SOUL_TOTEM = register("soul_totem", s -> new SoulTotemItem(s.maxCount(1)));
    public static final Item SOUL_JAM = register("soul_jam", s -> new SoulJamItem(s.maxCount(1)));

    private static <T extends Item> T register(String name, java.util.function.Function<Item.Settings, T> factory) {
        Identifier id = Identifier.of(Soulspire.MOD_ID, name);
        Item.Settings settings = new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id));
        T item = factory.apply(settings);
        return Registry.register(Registries.ITEM, id, item);
    }
    public static void registerModItems() {
        Soulspire.LOGGER.info("Registering Mod Items for " + Soulspire.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(SOUL_FRAGMENT);
            fabricItemGroupEntries.add(SOUL_SHARD);
            fabricItemGroupEntries.add(SOUL_AMULET);
            fabricItemGroupEntries.add(SOUL_CATALYST);
            fabricItemGroupEntries.add(SOUL_TOTEM);
            fabricItemGroupEntries.add(SOUL_JAM);

        });
    }
}

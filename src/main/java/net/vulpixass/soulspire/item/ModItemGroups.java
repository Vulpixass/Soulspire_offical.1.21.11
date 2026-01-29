package net.vulpixass.soulspire.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.vulpixass.soulspire.Soulspire;

public class ModItemGroups {
    public static ItemGroup SOULSPIRE_MAIN_GROUP;
    public static void registerItemGroups() {
        Soulspire.LOGGER.info("Registering Item Groups for " + Soulspire.MOD_ID);
        SOULSPIRE_MAIN_GROUP = Registry.register(Registries.ITEM_GROUP,
                Identifier.of(Soulspire.MOD_ID, "soulspire_main_items"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModItems.SOUL_AMULET))
                        .displayName(Text.translatable("itemgroup.soulspire.soulspire_main_items"))
                        .entries((displayContext, entries) -> {
                            entries.add(ModItems.SOUL_AMULET);
                            entries.add(ModItems.SOUL_FRAGMENT);
                            entries.add(ModItems.SOUL_SHARD);
                            entries.add(ModItems.SOUL_JAM);
                            entries.add(ModItems.SOUL_TOTEM);
                            entries.add(ModItems.SOUL_CATALYST);
                        }).build());
    }
}

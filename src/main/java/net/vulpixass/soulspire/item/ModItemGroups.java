package net.vulpixass.soulspire.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.vulpixass.soulspire.Soulspire;
import net.vulpixass.soulspire.block.ModBlocks;

public class ModItemGroups {
    public static ItemGroup SOULSPIRE_ITEM_GROUP;
    public static ItemGroup SOULSPIRE_BLOCK_GROUP;
    public static void registerItemGroups() {
        Soulspire.LOGGER.info("Registering Item Groups for " + Soulspire.MOD_ID);
        SOULSPIRE_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
                Identifier.of(Soulspire.MOD_ID, "soulspire_main_items"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModItems.SOUL_AMULET))
                        .displayName(Text.translatable("itemgroup.soulspire.soulspire_main_items"))
                        .entries((displayContext, entries) -> {
                            entries.add(ModItems.SOUL_AMULET);
                            entries.add(ModItems.SOUL_FRAGMENT);
                            entries.add(ModItems.SOUL_SHARD);
                            entries.add(ModItems.SOUL_ELIXIR);
                            entries.add(ModItems.SOUL_TOTEM);
                            entries.add(ModItems.SOUL_CATALYST);
                        }).build());
        SOULSPIRE_BLOCK_GROUP = Registry.register(Registries.ITEM_GROUP,
                Identifier.of(Soulspire.MOD_ID, "soulspire_main_blocks"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModBlocks.CHISELED_POLISHED_FACELESS_BLACKSTONE))
                        .displayName(Text.translatable("itemgroup.soulspire.soulspire_main_blocks"))
                        .entries((displayContext, entries) -> {
                            entries.add(ModBlocks.CHISELED_POLISHED_FACELESS_BLACKSTONE);

                            entries.add(ModBlocks.RUNE_EAST);
                            entries.add(ModBlocks.RUNE_NORTH);
                            entries.add(ModBlocks.RUNE_SOUTH);
                            entries.add(ModBlocks.RUNE_WEST);

                            entries.add(ModBlocks.RITUAL_ALTAR);
                        }).build());
    }
}

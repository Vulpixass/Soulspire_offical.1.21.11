package net.vulpixass.soulspire.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.vulpixass.soulspire.Soulspire;
import net.vulpixass.soulspire.block.custom.RitualAltarBlock;
import net.vulpixass.soulspire.block.custom.RuneBlock;
import net.vulpixass.soulspire.block.custom.SoulPowderDustBlock;

import java.util.function.Function;

public class ModBlocks {
    public static Block SOUL_POWDER = registerBlock("soul_powder",
            s -> new SoulPowderDustBlock(s.sounds(BlockSoundGroup.STONE).breakInstantly().dropsNothing().noCollision()
                    .luminance(SoulPowderDustBlock::getLuminace)));
    public static Block RITUAL_ALTAR = register("ritual_altar",
            s -> new RitualAltarBlock(s.sounds(BlockSoundGroup.STONE).strength(4, 8).requiresTool()));

    public static Block RUNE_NORTH = register("rune_north",
            s -> new RuneBlock(s.sounds(BlockSoundGroup.STONE).strength(4, 8).requiresTool().luminance(RuneBlock::getLuminace)));
    public static Block RUNE_SOUTH = register("rune_south",
            s -> new RuneBlock(s.sounds(BlockSoundGroup.STONE).strength(4, 8).requiresTool().luminance(RuneBlock::getLuminace)));
    public static Block RUNE_WEST = register("rune_west",
            s -> new RuneBlock(s.sounds(BlockSoundGroup.STONE).strength(4, 8).requiresTool().luminance(RuneBlock::getLuminace)));
    public static Block RUNE_EAST = register("rune_east",
            s -> new RuneBlock(s.sounds(BlockSoundGroup.STONE).strength(4, 8).requiresTool().luminance(RuneBlock::getLuminace)));
    public static Block CHISELED_POLISHED_FACELESS_BLACKSTONE = register("chiseled_polished_faceless_blackstone",
            s -> new Block(s.sounds(BlockSoundGroup.STONE).strength(4, 8).requiresTool()));

    public static void registerModBlocks() {
        Soulspire.LOGGER.info("Registering Mod Blocks for " + Soulspire.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(RITUAL_ALTAR);

            fabricItemGroupEntries.add(RUNE_NORTH);
            fabricItemGroupEntries.add(RUNE_SOUTH);
            fabricItemGroupEntries.add(RUNE_WEST);
            fabricItemGroupEntries.add(RUNE_EAST);
            fabricItemGroupEntries.add(CHISELED_POLISHED_FACELESS_BLACKSTONE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(RITUAL_ALTAR);

            fabricItemGroupEntries.add(RUNE_NORTH);
            fabricItemGroupEntries.add(RUNE_SOUTH);
            fabricItemGroupEntries.add(RUNE_WEST);
            fabricItemGroupEntries.add(RUNE_EAST);
        });
    }
    private static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> factory) {
        Identifier id = Identifier.of(Soulspire.MOD_ID, name);
        AbstractBlock.Settings blockSettings = AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, id));
        T block = factory.apply(blockSettings);
        Registry.register(Registries.BLOCK, id, block);
        Item.Settings itemSettings = new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id));
        Registry.register(Registries.ITEM, id, new BlockItem(block, itemSettings));
        return block;
    }
    private static <T extends Block> T registerBlock(String name, Function<AbstractBlock.Settings, T> factory) {
        Identifier id = Identifier.of(Soulspire.MOD_ID, name);
        AbstractBlock.Settings blockSettings = AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, id));
        T block = factory.apply(blockSettings);
        Registry.register(Registries.BLOCK, id, block);
        return block;
    }
}

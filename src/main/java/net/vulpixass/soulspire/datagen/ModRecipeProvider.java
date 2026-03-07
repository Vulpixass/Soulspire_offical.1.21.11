package net.vulpixass.soulspire.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.vulpixass.soulspire.block.ModBlocks;
import net.vulpixass.soulspire.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {

        return new RecipeGenerator(registryLookup, exporter) {
            private void simpleShapeless(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, int count, ItemConvertible input) {
                createShapeless(category, output, count)
                        .input(input)
                        .criterion(hasItem(input), conditionsFromItem(input))
                        .offerTo(exporter);
            }
            private void shapedRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, int count, String[] pattern, java.util.Map<Character, ItemConvertible> inputs) {
                var builder = createShaped(category, output, count);
                for (String line : pattern) {
                    builder.pattern(line);
                }
                for (var entry : inputs.entrySet()) {
                    builder.input(entry.getKey(), entry.getValue());
                    builder.criterion(hasItem(entry.getValue()), conditionsFromItem(entry.getValue()));
                }
                builder.offerTo(exporter);
            }
            private void simpleSmelting(RecipeExporter exporter, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
                offerSmelting(inputs, category, output, experience, cookingTime, group);
            }
            private void simpleBlasting(RecipeExporter exporter, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
                offerBlasting(inputs, category, output, experience, cookingTime, group);
            }

            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

                shapedRecipe(exporter, RecipeCategory.MISC, ModBlocks.CHISELED_POLISHED_FACELESS_BLACKSTONE, 2, new String[]{"P", "P"},
                        java.util.Map.of('P', Blocks.POLISHED_BLACKSTONE));
                }
        };
    }


    @Override
    public String getName() {
        return "";
    }
}
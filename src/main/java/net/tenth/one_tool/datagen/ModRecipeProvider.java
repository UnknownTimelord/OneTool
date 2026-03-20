package net.tenth.one_tool.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.tenth.one_tool.item.ModItems;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NonNull RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                ShapedRecipeJsonBuilder.create(Registries.ITEM, RecipeCategory.TOOLS, ModItems.ONE_TOOL)
                        .input('A', Items.GOLDEN_AXE)
                        .input('P', Items.GOLDEN_PICKAXE)
                        .input('S', Items.GOLDEN_SHOVEL)
                        .input('H', Items.GOLDEN_HOE)
                        .input('R', Items.REDSTONE_BLOCK)
                        .pattern("APS")
                        .pattern(" H ")
                        .pattern(" R ")
                        .criterion(hasItem(Items.GOLDEN_AXE), conditionsFromItem(Items.GOLDEN_AXE))
                        .criterion(hasItem(Items.GOLDEN_PICKAXE), conditionsFromItem(Items.GOLDEN_PICKAXE))
                        .criterion(hasItem(Items.GOLDEN_SHOVEL), conditionsFromItem(Items.GOLDEN_SHOVEL))
                        .criterion(hasItem(Items.GOLDEN_HOE), conditionsFromItem(Items.GOLDEN_HOE))
                        .offerTo(recipeExporter);
            }
        };
    }

    @Override
    public String getName() {
        return "One Tool Recipes";
    }
}

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
                        .input('A', Items.IRON_AXE)
                        .input('P', Items.IRON_PICKAXE)
                        .input('S', Items.IRON_SHOVEL)
                        .input('H', Items.IRON_HOE)
                        .input('R', Items.REDSTONE_BLOCK)
                        .pattern("APS")
                        .pattern(" H ")
                        .pattern(" R ")
                        .criterion(hasItem(Items.IRON_AXE), conditionsFromItem(Items.IRON_AXE))
                        .criterion(hasItem(Items.IRON_PICKAXE), conditionsFromItem(Items.IRON_PICKAXE))
                        .criterion(hasItem(Items.IRON_SHOVEL), conditionsFromItem(Items.IRON_SHOVEL))
                        .criterion(hasItem(Items.IRON_HOE), conditionsFromItem(Items.IRON_HOE))
                        .offerTo(recipeExporter);
            }
        };
    }

    @Override
    public String getName() {
        return "One Tool Recipes";
    }
}

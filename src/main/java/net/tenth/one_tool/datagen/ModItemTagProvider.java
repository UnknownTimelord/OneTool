package net.tenth.one_tool.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.@NonNull WrapperLookup lookup) {
//        getTagBuilder(TagKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("pickaxes")))
//                .add(Identifier.of(OneTool.MOD_ID, "one_tool"));
//        getTagBuilder(TagKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("axes")))
//                .add(Identifier.of(OneTool.MOD_ID, "one_tool"));
//        getTagBuilder(TagKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("shovels")))
//                .add(Identifier.of(OneTool.MOD_ID, "one_tool"));
//        getTagBuilder(TagKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("hoes")))
//                .add(Identifier.of(OneTool.MOD_ID, "one_tool"));
    }
}

package net.tenth.one_tool.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static final TagKey<Block> BREAKS_ALL = TagKey.of(RegistryKeys.BLOCK, Identifier.of(OneTool.MOD_ID, "breaks_all"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
       getTagBuilder(BREAKS_ALL)
                .addOptionalTag(Identifier.ofVanilla("mineable/axe"))
               .addOptionalTag(Identifier.ofVanilla("mineable/shovel"))
               .addOptionalTag(Identifier.ofVanilla("mineable/hoe"))
               .addOptionalTag(Identifier.ofVanilla("mineable/pickaxe"));
    }
}

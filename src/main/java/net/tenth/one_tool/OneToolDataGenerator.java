package net.tenth.one_tool;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.tenth.one_tool.datagen.ModBlockTagProvider;
import net.tenth.one_tool.datagen.ModItemTagProvider;
import net.tenth.one_tool.datagen.ModModelProvider;
import net.tenth.one_tool.datagen.ModRecipeProvider;
import org.jspecify.annotations.NonNull;

public class OneToolDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(@NonNull FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}

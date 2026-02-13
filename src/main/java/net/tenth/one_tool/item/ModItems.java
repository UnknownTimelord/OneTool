package net.tenth.one_tool.item;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;
import net.tenth.one_tool.datagen.ModBlockTagProvider;
import net.tenth.one_tool.item.custom.OneToolItem;

import java.util.function.Function;

public class ModItems {

    public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(OneTool.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void init() {
        OneTool.LOGGER.info("Init ModBlocks for {}", OneTool.MOD_ID);
    }

    public static final Item ONE_TOOL = registerItem("one_tool", OneToolItem::new, new Item.Settings()
            .tool(ToolMaterial.IRON, ModBlockTagProvider.BREAKS_ALL, 6.0F, -3.1F, 0)
            .customDamage((itemStack, i, entity, equipmentSlot, runnable) -> 0));
}

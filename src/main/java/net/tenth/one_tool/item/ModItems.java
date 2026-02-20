package net.tenth.one_tool.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.datagen.ModBlockTagProvider;
import net.tenth.one_tool.item.custom.OneToolItem;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.Constants;
import net.tenth.one_tool.util.GetToolDataHelper;

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
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(GetToolDataHelper.toolOf(OneToolTier.BASE, Constants.BASE_ENERGY));
            entries.add(GetToolDataHelper.toolOf(OneToolTier.BASE, 0));

            entries.add(GetToolDataHelper.toolOf(OneToolTier.DOUBLE, Constants.DOUBLE_ENERGY));
            entries.add(GetToolDataHelper.toolOf(OneToolTier.DOUBLE, 0));

            entries.add(GetToolDataHelper.toolOf(OneToolTier.TRIPLE, Constants.TRIPLE_ENERGY));
            entries.add(GetToolDataHelper.toolOf(OneToolTier.TRIPLE, 0));

            entries.add(GetToolDataHelper.toolOf(OneToolTier.QUADRUPLE, Constants.QUADRUPLE_ENERGY));
            entries.add(GetToolDataHelper.toolOf(OneToolTier.QUADRUPLE, 0));
        });
    }

    public static final Item ONE_TOOL = registerItem("one_tool", OneToolItem::new, new Item.Settings()
            .enchantable(1)
            .food(new FoodComponent(1, 0, true))
            .tool(ToolMaterial.IRON, ModBlockTagProvider.BREAKS_ALL,
                    Constants.BASE_ATK_DAMAGE, Constants.BASE_ATK_SPEED, Constants.DISABLE_BLOCK_FOR_SEC)
            .customDamage((itemStack, i, entity, equipmentSlot, runnable) -> 0)
            .component(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE)
            .component(ModDataComponentTypes.ENERGY, Constants.BASE_ENERGY));
}

package net.tenth.one_tool.util;

import net.minecraft.item.ItemStack;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.item.ModItems;
import net.tenth.one_tool.types.OneToolTier;

public class GetToolDataHelper {
    public static OneToolTier getToolTier(ItemStack tool) {
        return tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE);
    }
    public static OneToolTier getPreviousTier(ItemStack tool) {
        return tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE).getPrevious();
    }
    public static int getMaxEnergy(ItemStack tool) {
        OneToolTier tier = getToolTier(tool);
        return tier == OneToolTier.BASE
                ? Constants.BASE_ENERGY
                : tier == OneToolTier.DOUBLE
                ? Constants.DOUBLE_ENERGY
                : tier == OneToolTier.TRIPLE
                ? Constants.TRIPLE_ENERGY
                : Constants.QUADRUPLE_ENERGY;
    }
    public static int getEnergy(ItemStack tool) {
        return tool.getOrDefault(ModDataComponentTypes.ENERGY, 0);
    }
    public static ItemStack toolOf(OneToolTier tier, int energy) {
        ItemStack tool = new ItemStack(ModItems.ONE_TOOL);
        tool.set(ModDataComponentTypes.ONE_TOOL_TIER, tier);

        int maxEnergy = Constants.MAX_TO_TIER_ENERGY.get(tier);
        int clampedEnergy = Math.max(0, Math.min(energy, maxEnergy));

        tool.set(ModDataComponentTypes.ENERGY, clampedEnergy);
        return tool;
    }
}

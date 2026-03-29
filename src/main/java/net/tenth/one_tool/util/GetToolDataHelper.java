package net.tenth.one_tool.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
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
        tool.set(DataComponentTypes.FOOD, new FoodComponent(0, 0, false));
        return tool;
    }
    public static boolean hasEnergy(ItemStack stack) {
        return getEnergy(stack) > 0;
    }
    public static int getEnergyColor(ItemStack stack, int pct) {
        int startColor = Colors.GREEN;
        int color = MiscHelper.interpolateColor(startColor, 0xFFFFFF55, 0xFFFF5555, pct);

        long mis = Util.getMeasuringTimeMs();
        float t = (mis % 1200L) / 1200f;
        float pulse = 2F * (float) Math.sin(t * Math.PI);
        int pulse_color = MiscHelper.interpolateColor(Colors.BLACK, color, pulse);

        if (GetToolDataHelper.getEnergy(stack) == 0) {
            color = pulse_color;
        }
        return color;
    }
    public static int getXpColor(ItemStack stack, int pct) {
        int startColor = Colors.BLUE;
        int color = MiscHelper.interpolateColor(startColor, Colors.GREEN, pct);

        long mis = Util.getMeasuringTimeMs();
        float t = (mis % 1200L) / 1200f;
        float pulse = 2F * (float) Math.sin(t * Math.PI);
        int pulse_color = MiscHelper.interpolateColor(Colors.BLACK, color, pulse);
        int maxEnergy = getMaxEnergy(stack);

        if (GetToolDataHelper.getXP(stack) >= maxEnergy - (maxEnergy / 4)) {
            color = pulse_color;
        }
        return color;
    }

    public static int getMaxInvSize(ItemStack tool) {
        return switch (getToolTier(tool)) {
            case BASE -> Constants.BASE_INV_SIZE;
            case DOUBLE -> Constants.DOUBLE_INV_SIZE;
            case TRIPLE -> Constants.TRIPLE_INV_SIZE;
            case QUADRUPLE -> Constants.QUADRUPLE_INV_SIZE;
        };
    }

    public static int getXP(ItemStack tool) {
        return tool.getOrDefault(ModDataComponentTypes.XP, 0);
    }
}

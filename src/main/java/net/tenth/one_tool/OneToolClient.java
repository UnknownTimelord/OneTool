package net.tenth.one_tool;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.tenth.one_tool.item.custom.OneToolItem;
import net.tenth.one_tool.screen.ModScreenHandlers;
import net.tenth.one_tool.screen.custom.OneToolScreen;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.GetToolDataHelper;
import net.tenth.one_tool.util.MiscHelper;

public class OneToolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.ONE_TOOL, OneToolScreen::new);
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, type, textConsumer) -> {
            if (!(stack.getItem() instanceof OneToolItem)) return;

            if (type.isAdvanced()) {
                textConsumer.add(1, Text.translatable("item.one_tool.advanced_tooltip1").formatted(Formatting.GRAY));
                textConsumer.add(1, Text.translatable("item.one_tool.advanced_tooltip2").formatted(Formatting.GRAY));
            } else {
                textConsumer.add(1, Text.translatable("item.one_tool.more_info").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
            }

            int maxEnergy = GetToolDataHelper.getMaxEnergy(stack);
            int energy = GetToolDataHelper.getEnergy(stack);
            OneToolTier tier = GetToolDataHelper.getToolTier(stack);

            textConsumer.add(1, Text.translatable("one_tool.tier.tooltip")
                    .append(Text.translatable("one_tool.tier_value.tooltip", tier.asRoman()).formatted(Formatting.AQUA)));

            int xp = GetToolDataHelper.getXP(stack);
            int requiredXp = tier.asInt() * maxEnergy;

            Integer pct = MiscHelper.xpPercent(stack);

            int xpColor
                    = pct != null
                    ? GetToolDataHelper.getXpColor(stack, pct)
                    : Colors.RED;

            textConsumer.add(1, Text.translatable("one_tool.xp.tooltip")
                    .append(Text.translatable("one_tool.energy_value.tooltip", xp, requiredXp).withColor(xpColor)));

            pct = MiscHelper.energyPercent(stack);
            int energyColor
                    = pct != null
                    ? GetToolDataHelper.getEnergyColor(stack, pct)
                    : Colors.GREEN;

            if (energy != 0) {
                textConsumer.add(1, Text.translatable("one_tool.energy.tooltip")
                        .append(Text.translatable("one_tool.energy_value.tooltip", energy, maxEnergy).withColor(energyColor)));
            }
            else {
                textConsumer.add(1, Text.translatable("one_tool.energy.tooltip")
                        .append(Text.translatable("one_tool.empty.tooltip")
                                .withColor(energyColor)
                                .formatted(Formatting.BOLD)
                        )
                );
            }

        });
    }
}

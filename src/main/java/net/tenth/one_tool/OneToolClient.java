package net.tenth.one_tool;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.tenth.one_tool.component.ModDataComponentTypes;
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

            PlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null && type.isAdvanced()) {
                boolean eaten = player.getOrDefault(ModDataComponentTypes.HAS_EATEN_ONE_TOOL, false);

                if (!eaten) {
                    Text t = Text.translatable("one_tool.hasnt_eaten.tooltip");

                    textConsumer.addAll(1, MiscHelper.splitOnNewlines(t, Formatting.GOLD));
                } else {
                    textConsumer.add(1,
                            Text.translatable(
                                    "one_tool.has_eaten.tooltip"
                            ).formatted(Formatting.GOLD)
                    );
                }
            }

            int maxEnergy = GetToolDataHelper.getMaxEnergy(stack);
            int energy = GetToolDataHelper.getEnergy(stack);
            OneToolTier tier = GetToolDataHelper.getToolTier(stack);

            textConsumer.add(1, Text.translatable("one_tool.tier.tooltip")
                    .append(Text.translatable("one_tool.tier_value.tooltip", tier.asRoman()).formatted(Formatting.AQUA)));

            Integer pct = MiscHelper.percent(stack);
            int color
                    = pct != null
                    ? GetToolDataHelper.getEnergyColor(stack, pct)
                    : Colors.GREEN;

            if (energy != 0) {
                textConsumer.add(1, Text.translatable("one_tool.energy.tooltip")
                        .append(Text.translatable("one_tool.energy_value.tooltip", energy, maxEnergy).withColor(color)));
            }
            else {
                textConsumer.add(1, Text.translatable("one_tool.energy.tooltip")
                        .append(Text.translatable("one_tool.empty.tooltip")
                                .withColor(color)
                                .formatted(Formatting.BOLD)
                        )
                );
            }
        });
    }
}

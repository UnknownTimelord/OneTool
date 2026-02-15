package net.tenth.one_tool;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.tenth.one_tool.screen.ModScreenHandlers;
import net.tenth.one_tool.screen.custom.OneToolScreen;

public class OneToolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.ONE_TOOL, OneToolScreen::new);
    }
}

package net.tenth.one_tool.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;
import net.tenth.one_tool.screen.custom.OneToolScreenHandler;

public class ModScreenHandlers {
    public static final ScreenHandlerType<OneToolScreenHandler> ONE_TOOL =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(OneTool.MOD_ID, "one_tool"),
                    new ExtendedScreenHandlerType<>(OneToolScreenHandler::new, ItemStack.PACKET_CODEC));

    public static void init() {
        OneTool.LOGGER.info("Registering Mod Screen Handlers for {}", OneTool.MOD_ID);
    }
}

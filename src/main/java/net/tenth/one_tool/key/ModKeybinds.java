package net.tenth.one_tool.key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static KeyBinding TOOL_PICKUP;
    public static KeyBinding TOOL_INV;
    public static KeyBinding.Category TOOL_CATEGORY =
            KeyBinding.Category.create(Identifier.of(OneTool.MOD_ID, "general"));

    public static void init() {
        TOOL_PICKUP = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.one_tool.tool_pickup",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_K,
                        TOOL_CATEGORY
                )
        );
        TOOL_INV = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.one_tool.open_inventory",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_B,
                        TOOL_CATEGORY
                )
        );
    }
}

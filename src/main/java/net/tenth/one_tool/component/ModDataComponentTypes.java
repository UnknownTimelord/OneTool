package net.tenth.one_tool.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;
import net.tenth.one_tool.types.OneToolTier;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<OneToolTier> ONE_TOOL_TIER =
            register("one_tool_tier", builder -> builder.codec(OneToolTier.CODEC));

    public static final ComponentType<Integer> ENERGY =
            register("one_tool_energy", builder -> builder.codec(Codec.INT));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(OneTool.MOD_ID, name),
                builder.apply(ComponentType.builder()).build());
    }

    public static void init() {
        OneTool.LOGGER.info("Initializing Mod Data Components for {}", OneTool.MOD_ID);
    }
}

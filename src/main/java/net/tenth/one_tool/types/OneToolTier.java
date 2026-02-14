package net.tenth.one_tool.types;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum OneToolTier implements StringIdentifiable {
    BASE("base"),
    DOUBLE("double"),
    TRIPLE("triple"),
    QUADRUPLE("quadruple");

    public static final Codec<OneToolTier> CODEC =
            StringIdentifiable.createCodec(OneToolTier::values);

    private final String id;

    OneToolTier(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return id;
    }

    public int asInt() {
        return switch(id) {
            case "base" -> 1;
            case "double" -> 2;
            case "triple" -> 3;
            case "quadruple" -> 4;
            default -> 0;
        };
    }
}

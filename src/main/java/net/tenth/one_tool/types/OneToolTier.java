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

    public String asRoman() {
        return switch (id) {
            case "base" -> "I";
            case "double" -> "II";
            case "triple" -> "III";
            case "quadruple" -> "IV";
            default -> "X";
        };
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

    public OneToolTier getPrevious() {
        return switch (id) {
            case "triple" -> DOUBLE;
            case "quadruple" -> TRIPLE;
            default -> BASE;
        };
    }
    public OneToolTier getNext() {
        return switch (this) {
            case BASE -> DOUBLE;
            case DOUBLE -> TRIPLE;
            case TRIPLE -> QUADRUPLE;
            case QUADRUPLE -> QUADRUPLE; // clamp at max tier
        };
    }
}

package net.tenth.one_tool.util;

import net.tenth.one_tool.types.OneToolTier;

import java.util.Map;

public class Constants {
    public static final float BASE_ATK_DAMAGE = 6.0F;
    public static final float BASE_ATK_SPEED = -3.1F;
    public static final float DISABLE_BLOCK_FOR_SEC = 0F;
    public static final int BASE_ENERGY = 1024;
    public static final int DOUBLE_ENERGY = BASE_ENERGY * 2;
    public static final int TRIPLE_ENERGY = BASE_ENERGY * 3;
    public static final int QUADRUPLE_ENERGY = BASE_ENERGY * 4;
    public static final int BASE_INV_SIZE = 27;
    public static final int DOUBLE_INV_SIZE = BASE_INV_SIZE * 2;
    public static final int TRIPLE_INV_SIZE = BASE_INV_SIZE * 3;
    public static final int QUADRUPLE_INV_SIZE = BASE_INV_SIZE * 4;
    public static final Map<OneToolTier, Integer> MAX_TO_TIER_ENERGY = Map.of(
            OneToolTier.BASE, BASE_ENERGY,
            OneToolTier.DOUBLE, DOUBLE_ENERGY,
            OneToolTier.TRIPLE, TRIPLE_ENERGY,
            OneToolTier.QUADRUPLE, QUADRUPLE_ENERGY
    );
}

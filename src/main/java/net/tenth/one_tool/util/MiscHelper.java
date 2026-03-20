package net.tenth.one_tool.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import java.util.ArrayList;
import java.util.List;

public class MiscHelper {
    public static int interpolateColor(int startColor, int midColor, int endColor, float percent) {
        // Clamp and normalize
        percent = Math.max(0f, Math.min(percent, 100f)) / 100f;

        int fromColor, toColor;
        float localT;

        // Split halfway: 0–50% (start→mid), 50–100% (mid→end)
        if (percent < 0.5f) {
            fromColor = endColor;
            toColor = midColor;
            localT = percent / 0.5f; // map 0–0.5 → 0–1
        } else {
            fromColor = midColor;
            toColor = startColor;
            localT = (percent - 0.5f) / 0.5f; // map 0.5–1 → 0–1
        }

        // Extract channels
        int a1 = (fromColor >> 24) & 0xFF;
        int r1 = (fromColor >> 16) & 0xFF;
        int g1 = (fromColor >> 8) & 0xFF;
        int b1 = fromColor & 0xFF;

        int a2 = (toColor >> 24) & 0xFF;
        int r2 = (toColor >> 16) & 0xFF;
        int g2 = (toColor >> 8) & 0xFF;
        int b2 = toColor & 0xFF;

        // Linear interpolation
        int a = Math.round(a1 + (a2 - a1) * localT);
        int r = Math.round(r1 + (r2 - r1) * localT);
        int g = Math.round(g1 + (g2 - g1) * localT);
        int b = Math.round(b1 + (b2 - b1) * localT);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int interpolateColor(int startColor, int endColor, float percent) {
        percent = Math.max(0, Math.min(1, percent));

        int aA = (startColor >> 24) & 0xFF;
        int rA = (startColor >> 16) & 0xFF;
        int gA = (startColor >> 8) & 0xFF;
        int bA = startColor & 0xFF;

        int aB = (endColor >> 24) & 0xFF;
        int rB = (endColor >> 16) & 0xFF;
        int gB = (endColor >> 8) & 0xFF;
        int bB = endColor & 0xFF;

        int a = Math.round(aA * (1 - percent) + aB * percent);
        int r = Math.round(rA * (1 - percent) + rB * percent);
        int g = Math.round(gA * (1 - percent) + gB * percent);
        int b = Math.round(bA * (1 - percent) + bB * percent);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    public static void drawItemBar(DrawContext ctx, ItemStack stack, int x, int y) {
        if (stack.isItemBarVisible()) {
            int i = x + 2;
            int j = y + 13;
            fill(ctx, RenderPipelines.GUI, i, j, i + 13, j + 2, Colors.BLACK);
            fill(ctx, RenderPipelines.GUI, i, j, i + stack.getItemBarStep(), j + 1, ColorHelper.fullAlpha(stack.getItemBarColor()));
        }
    }
    public static void fill(DrawContext ctx, RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        fill(ctx, pipeline, TextureSetup.empty(), x1, y1, x2, y2, color, null);
    }

    private static void fill(DrawContext ctx, RenderPipeline pipeline, TextureSetup textureSetup, int x1, int y1, int x2, int y2, int color, @Nullable Integer color2) {
        ctx.state
                .addSimpleElement(
                        new ColoredQuadGuiElementRenderState(
                                pipeline, textureSetup, new Matrix3x2f(ctx.getMatrices()), x1, y1, x2, y2, color, color2 != null ? color2 : color, ctx.scissorStack.peekLast()
                        )
                );
    }

    public static Integer energyPercent(ItemStack stack) {
        int max = GetToolDataHelper.getMaxEnergy(stack);
        if (max <= 0) return null;

        int curr = Math.max(0, Math.min(max, GetToolDataHelper.getEnergy(stack)));

        return Math.round(curr * 100f / max);
    }

    public static Integer xpPercent(ItemStack stack) {
        int max = GetToolDataHelper.getToolTier(stack).asInt() * GetToolDataHelper.getMaxEnergy(stack);
        if (max <= 0) return null;

        int curr = Math.max(0, Math.min(max, GetToolDataHelper.getXP(stack)));

        return Math.round(curr * 100f / max);
    }

    public static int getMissingHunger(PlayerEntity player) {
        return 20 - player.getHungerManager().getFoodLevel();
    }

    public static List<Text> splitOnNewlines(Text text, Formatting formatting) {
        String s = text.getString();
        String[] parts = s.split("\n");
        List<Text> out = new ArrayList<>();
        for (String part : parts) {
            out.add(Text.literal(part).formatted(formatting));
        }
        return out;
    }
}

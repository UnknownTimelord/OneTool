package net.tenth.one_tool.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.tenth.one_tool.item.custom.OneToolItem;
import net.tenth.one_tool.util.GetToolDataHelper;
import net.tenth.one_tool.util.MiscHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Redirect(
            method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItemBar(Lnet/minecraft/item/ItemStack;II)V"
            )
    )
    private void energetic_tools$skipBarForCustom(DrawContext self, ItemStack stack, int x, int y) {
        if (!(stack.getItem() instanceof OneToolItem)) {
            MiscHelper.drawItemBar(self, stack, x, y);
        }
        else {
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;
            Integer pct = MiscHelper.percent(stack);
            if (pct == null) return;

            String s = pct + "%";
            int w = tr.getWidth(s);
            float SCALE = 0.5f;
            int localX = Math.round(16f / SCALE - w);
            int localY = Math.round(9f  / SCALE);
            localY += 6;
            var ms  = self.getMatrices();
            int startColor;
            startColor = Colors.GREEN;
            int color = MiscHelper.interpolateColor(startColor, 0xFFFFFF55, 0xFFFF5555, pct);

            long mis = Util.getMeasuringTimeMs();
            float t = (mis % 1200L) / 1200f;
            float pulse = 2F * (float) Math.sin(t * Math.PI);
            int pulse_color = MiscHelper.interpolateColor(Colors.BLACK, color, pulse);

            if (GetToolDataHelper.getEnergy(stack) == 0) {
                color = pulse_color;
            }

            ms.pushMatrix();
            ms.translate(x, y);
            ms.scale(SCALE, SCALE);
            self.drawText(tr, s, localX, localY, color, true);
            self.drawText(tr, GetToolDataHelper.getToolTier(stack).asRoman(), 0, 0, Colors.CYAN, true);
            ms.popMatrix();
        }
    }
}

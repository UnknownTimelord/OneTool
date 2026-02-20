package net.tenth.one_tool.screen.custom;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;
import net.tenth.one_tool.util.Constants;

public class OneToolScreen extends HandledScreen<OneToolScreenHandler> {
    public static final Identifier BASE_INV = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
    public static final Identifier DOUBLE_INV = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    public static final Identifier TRIPLE_INV = Identifier.of(OneTool.MOD_ID, "textures/gui/container/triple_inv.png");
    public static final Identifier QUAD_INV = Identifier.of(OneTool.MOD_ID, "textures/gui/container/quad_inv.png");


    public OneToolScreen(OneToolScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        if (handler.toolInventory.size() == Constants.QUADRUPLE_INV_SIZE) {
            this.backgroundWidth = 338;
            this.backgroundHeight = 222;
            this.playerInventoryTitleY = this.backgroundHeight - 94; // vanilla layout
            this.playerInventoryTitleX += 82;
        } else if (handler.toolInventory.size() == Constants.TRIPLE_INV_SIZE) {
            this.backgroundWidth = 176;
            this.backgroundHeight = 276;
            this.playerInventoryTitleY = this.backgroundHeight - 94; // vanilla layout
        } else if (handler.toolInventory.size() == Constants.DOUBLE_INV_SIZE) {
            this.backgroundWidth = 176;
            this.backgroundHeight = 222;
            this.playerInventoryTitleY = this.backgroundHeight - 94; // vanilla layout
        } else {
            this.backgroundWidth = 176;
            this.backgroundHeight = 166;
            this.playerInventoryTitleY = this.backgroundHeight - 94;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        Identifier TO_DRAW
                = handler.toolInventory.size() == Constants.DOUBLE_INV_SIZE
                ? DOUBLE_INV
                : handler.toolInventory.size() == Constants.TRIPLE_INV_SIZE
                ? TRIPLE_INV
                : handler.toolInventory.size() == Constants.QUADRUPLE_INV_SIZE
                ? QUAD_INV
                : BASE_INV;

        int size = TO_DRAW != QUAD_INV && TO_DRAW != TRIPLE_INV ? 256 : 512;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TO_DRAW, x, y, 0, 0, backgroundWidth, backgroundHeight, size, size);
    }
}

package net.tenth.one_tool.screen.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.inventory.OneToolInventory;
import net.tenth.one_tool.screen.ModScreenHandlers;
import net.tenth.one_tool.util.Constants;

import java.util.List;

public class OneToolScreenHandler extends ScreenHandler {
    OneToolInventory toolInventory;

    public OneToolScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack tool) {
        super(ModScreenHandlers.ONE_TOOL, syncId);
        toolInventory = tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_INV,
                new OneToolInventory(Constants.BASE_INV_SIZE, List.of()));

        int startX = 0;
        int startY = 0;
        int spacing = 18;

        int rows = toolInventory.size() / 9;
        int index = 0;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(toolInventory, index++,
                        startX + x * spacing,
                        startY + y * spacing
                ));
            }
        }

        this.addPlayerInventorySlots(playerInventory, 8, 84);
        this.addPlayerHotbarSlots(playerInventory, 8, 142);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack stackInSlot = slot.getStack();
            original = stackInSlot.copy();

            int toolInvSize = this.toolInventory.size();
            int playerInvStart = toolInvSize;
            int playerInvEnd = playerInvStart + 27;
            int hotbarStart = playerInvEnd;
            int hotbarEnd = hotbarStart + 9;

            if (slotIndex < toolInvSize) {
                // Tool -> player (prefer main, then hotbar)
                if (!this.insertItem(stackInSlot, playerInvStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < hotbarStart) {
                // Player main -> tool
                if (!this.insertItem(stackInSlot, 0, toolInvSize, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Hotbar -> tool
                if (!this.insertItem(stackInSlot, 0, toolInvSize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return original;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return toolInventory.canPlayerUse(player);
    }
}

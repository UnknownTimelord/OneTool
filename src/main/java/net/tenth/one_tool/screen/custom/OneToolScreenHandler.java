package net.tenth.one_tool.screen.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.inventory.OneToolChargeSlot;
import net.tenth.one_tool.inventory.OneToolInventory;
import net.tenth.one_tool.inventory.OneToolSlot;
import net.tenth.one_tool.item.custom.OneToolItem;
import net.tenth.one_tool.screen.ModScreenHandlers;
import net.tenth.one_tool.util.Constants;

import java.util.List;

public class OneToolScreenHandler extends ScreenHandler {
    OneToolInventory toolInventory;

    public OneToolScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack tool) {
        super(ModScreenHandlers.ONE_TOOL, syncId);
        toolInventory = tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_INV,
                new OneToolInventory(Constants.BASE_INV_SIZE, List.of()));

        int startX = 8;
        int startY = 8;
        int spacing = 18;

        int totalSlots = toolInventory.size();

        int cols;
        int rows;

        switch (totalSlots) {
            case Constants.BASE_INV_SIZE -> { cols = 9;  rows = 2; }
            case Constants.DOUBLE_INV_SIZE -> { cols = 9;  rows = 4; }
            case Constants.TRIPLE_INV_SIZE -> { cols = 9;  rows = 8; }
            case Constants.QUADRUPLE_INV_SIZE -> { cols = 18; rows = 8; }
            default -> throw new IllegalStateException("Unsupported tool inventory size: " + totalSlots);
        }

        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (index >= totalSlots) break;

                this.addSlot(new OneToolSlot(
                        toolInventory,
                        index++,
                        startX + x * spacing,
                        startY + y * spacing
                ));
            }
        }

        int charge_x
                = totalSlots == Constants.DOUBLE_INV_SIZE
                ? 80
                : totalSlots == Constants.QUADRUPLE_INV_SIZE
                ? 162
                : 80;

        int charge_y
                = totalSlots == Constants.DOUBLE_INV_SIZE
                ? 82
                : totalSlots == Constants.TRIPLE_INV_SIZE
                ? 118
                : totalSlots == Constants.QUADRUPLE_INV_SIZE
                ? 86
                : 50;

        SimpleInventory charge_inv = new SimpleInventory(1);
        addSlot(new OneToolChargeSlot(tool, charge_inv, 0, charge_x, charge_y));

        int inv_y
                = totalSlots == Constants.DOUBLE_INV_SIZE
                ? 102
                : totalSlots == Constants.TRIPLE_INV_SIZE
                ? 138
                : totalSlots == Constants.QUADRUPLE_INV_SIZE
                ? 110
                : 74;
        int hotbar_y
                = totalSlots == Constants.DOUBLE_INV_SIZE
                ? 160
                : totalSlots == Constants.TRIPLE_INV_SIZE
                ? 196
                : totalSlots == Constants.QUADRUPLE_INV_SIZE
                ? 168
                : 132;
        int x
                = totalSlots != Constants.QUADRUPLE_INV_SIZE
                ? 8
                : 90;

        this.addPlayerInventorySlots(playerInventory, x, inv_y);
        this.addPlayerHotbarSlots(playerInventory, x, hotbar_y);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasStack() && slot.getStack().getItem() instanceof OneToolItem) {
            return original;
        }

        if (slot != null && slot.hasStack()) {
            ItemStack stackInSlot = slot.getStack();
            original = stackInSlot.copy();

            int toolInvSize = this.toolInventory.size();
            int playerInvStart = toolInvSize;
            int playerInvEnd = playerInvStart + 27;
            int hotbarStart = playerInvEnd;
            int hotbarEnd = hotbarStart + 9;

            if (slotIndex < toolInvSize) {
                if (!this.insertItem(stackInSlot, playerInvStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < hotbarStart) {
                if (!this.insertItem(stackInSlot, 0, toolInvSize, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

package net.tenth.one_tool.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.tenth.one_tool.item.custom.OneToolItem;

public class OneToolSlot extends Slot {
    public OneToolSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return !(stack.getItem() instanceof OneToolItem) && super.canInsert(stack);
    }
}

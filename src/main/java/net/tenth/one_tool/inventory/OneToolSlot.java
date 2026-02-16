package net.tenth.one_tool.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.tenth.one_tool.item.custom.OneToolItem;

public class OneToolSlot extends Slot {
    private int index;

    public OneToolSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.index = index;
    }

    public OneToolSlot move(int x, int y) {
        return new OneToolSlot(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return !(stack.getItem() instanceof OneToolItem) && super.canInsert(stack);
    }
}

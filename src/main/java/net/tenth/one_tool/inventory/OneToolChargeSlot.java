package net.tenth.one_tool.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.util.GetToolDataHelper;

public class OneToolChargeSlot extends Slot {
    private ItemStack tool;
    public OneToolChargeSlot(ItemStack tool, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.tool = tool;
    }

    @Override
    public ItemStack insertStack(ItemStack stack) {
        return this.insertStack(stack, stack.getCount());
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        if (stack.isEmpty() || count <= 0) return stack;

        if (this.tool == null || this.tool.isEmpty()) return stack;

        int perItem;
        if (stack.isOf(Items.REDSTONE)) {
            perItem = 1;
        } else if (stack.isOf(Items.REDSTONE_BLOCK)) {
            perItem = 9;
        } else {
            return stack;
        }

        int availableItems = Math.min(count, stack.getCount());

        int energy = GetToolDataHelper.getEnergy(tool);
        int max = GetToolDataHelper.getMaxEnergy(tool);
        int space = Math.max(0, max - energy);

        int offeredEnergy = availableItems * perItem;

        int acceptedEnergy = Math.min(space, offeredEnergy);

        int itemsToConsume = acceptedEnergy / perItem;

        if (itemsToConsume <= 0) return stack;

        int newEnergy = energy + itemsToConsume * perItem;
        tool.set(ModDataComponentTypes.ENERGY, Math.min(max, newEnergy));

        stack.decrement(itemsToConsume);
        return stack;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return (stack.isOf(Items.REDSTONE) || stack.isOf(Items.REDSTONE_BLOCK)) && super.canInsert(stack);
    }
}

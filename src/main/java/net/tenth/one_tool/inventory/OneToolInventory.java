package net.tenth.one_tool.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.DefaultedList;
import net.tenth.one_tool.OneTool;
import net.tenth.one_tool.item.custom.OneToolItem;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class OneToolInventory implements Inventory {
    public DefaultedList<ItemStack> items;

    public OneToolInventory(int size, List<ItemStack> stacks) {
        this.items = DefaultedList.ofSize(size, ItemStack.EMPTY);
        for (int i = 0; i < Math.min(size, stacks.size()); i++) {
            this.items.set(i, stacks.get(i));
        }
    }

    public OneToolInventory(OneToolTier tier) {
        int size = switch (tier) {
            case BASE -> Constants.BASE_INV_SIZE;
            case DOUBLE -> Constants.DOUBLE_INV_SIZE;
            case TRIPLE -> Constants.TRIPLE_INV_SIZE;
            default -> Constants.QUADRUPLE_INV_SIZE;
        };
        items = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public static final MapCodec<OneToolInventory> MAP_CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.INT.fieldOf("size").forGetter(inv -> inv.items.size()),
                    ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(inv -> inv.items)
            ).apply(inst, OneToolInventory::new));

    public static final Codec<OneToolInventory> CODEC = MAP_CODEC.codec();

    public static final PacketCodec<RegistryByteBuf, OneToolInventory> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER,
                    inv -> Math.min(inv.items.size(), Constants.QUADRUPLE_INV_SIZE),

                    PacketCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_PACKET_CODEC, Constants.QUADRUPLE_INV_SIZE),
                    inv -> inv.items.subList(0, Math.min(inv.items.size(), Constants.QUADRUPLE_INV_SIZE)),

                    OneToolInventory::new
            );

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stackInSlot = items.get(slot);
        if (stackInSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = stackInSlot.split(amount);

        if (stackInSlot.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
        }

        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    public boolean insertStack(ItemStack stack) {
        for (int i = 0; i < this.size(); i++) {
            ItemStack existing = this.items.get(i);

            OneTool.LOGGER.info("This should not bw an empty stack: {}", this.items.get(i));

            if (!existing.isEmpty()
                    && ItemStack.areItemsAndComponentsEqual(existing, stack)
                    && existing.getCount() < existing.getMaxCount()) {
                int space = existing.getMaxCount() - existing.getCount();
                int moved = Math.min(space, stack.getCount());
                existing.increment(moved);
                stack.decrement(moved);
                if (stack.isEmpty()) {
                    OneTool.LOGGER.info("This should 1 more {}: {}", stack.getItem(), this.items.get(i));
                    return true;
                }
            }
        }

        for (int i = 0; i < this.size(); i++) {
            if (this.items.get(i).isEmpty()) {
                OneTool.LOGGER.info("This should be an empty stack: {}", this.items.get(i));
                this.items.set(i, stack.copy());
                OneTool.LOGGER.info("This should 1 of {}: {}", stack.getItem(), this.items.get(i));
                stack.setCount(0);
                return true;
            }
        }

        return false;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof OneToolItem;
    }

    @Override
    public void clear() {
        items.clear();
    }
}

package net.tenth.one_tool.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.inventory.OneToolInventory;
import net.tenth.one_tool.item.custom.OneToolItem;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.GetToolDataHelper;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockDropsMixin {
    @Inject(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void dropStacks(
            BlockState state,
            World world,
            BlockPos pos,
            @Nullable BlockEntity blockEntity,
            @Nullable Entity entity,
            ItemStack tool,
            CallbackInfo ci
    ) {
        if (world instanceof ServerWorld && tool.getItem() instanceof OneToolItem) {
            boolean doPickup = tool.getOrDefault(ModDataComponentTypes.PICKUP, false);
            if (!doPickup) {
                return;
            }

            OneToolTier tier = GetToolDataHelper.getToolTier(tool);
            OneToolInventory baseInv = tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_INV, new OneToolInventory(tier));
            OneToolInventory inv = tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_INV, new OneToolInventory(tier));
            Block.getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, tool).forEach(itemStack -> {
                inv.insertStack(itemStack);
                if (!itemStack.isEmpty()) {
                    if (entity instanceof PlayerEntity player) {
                        if (player.getInventory().getEmptySlot() == -1) {
                            player.sendMessage(Text.translatable("one_tool.full"), false);
                            Block.dropStack(world, pos, itemStack);
                        } else {
                            player.getInventory().insertStack(itemStack);
                        }
                    } else {
                        Block.dropStack(world, pos, itemStack);
                    }
                }
            });
            if (!inv.equals(baseInv)) {
                tool.set(ModDataComponentTypes.ONE_TOOL_INV, inv);
            }
            state.onStacksDropped((ServerWorld)world, pos, tool, true);
            ci.cancel();
        }
    }
}
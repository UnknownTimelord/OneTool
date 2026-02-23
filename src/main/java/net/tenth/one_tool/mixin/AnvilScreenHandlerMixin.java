package net.tenth.one_tool.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.item.ModItems;
import net.tenth.one_tool.types.OneToolTier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {

    @Shadow @Final private Property levelCost;

    // If you want vanilla to decrement the right item properly on take:
    @Shadow private int repairItemUsage;

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void one_tool$upgradeTier(CallbackInfo ci) {
        ScreenHandler self = (ScreenHandler)(Object)this;

        ItemStack left  = self.getSlot(AnvilScreenHandler.INPUT_1_ID).getStack();
        ItemStack right = self.getSlot(AnvilScreenHandler.INPUT_2_ID).getStack();

        if (left.isEmpty() || right.isEmpty()) return;
        if (!left.isOf(ModItems.ONE_TOOL)) return;

        // your upgrade material (change as needed)
        if (!right.isOf(Items.NETHERITE_INGOT)) return;

        OneToolTier cur = left.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE);
        OneToolTier next = cur.getNext();

        // at max tier -> no output
        if (next == cur) {
            self.getSlot(AnvilScreenHandler.OUTPUT_ID).setStack(ItemStack.EMPTY);
            this.levelCost.set(0);
            ci.cancel();
            return;
        }

        ItemStack result = left.copy();
        result.set(ModDataComponentTypes.ONE_TOOL_TIER, next);

        self.getSlot(AnvilScreenHandler.OUTPUT_ID).setStack(result);

        // cost + consume exactly 1 material item on take
        this.levelCost.set(5);
        this.repairItemUsage = 1;

        ci.cancel();
    }
}
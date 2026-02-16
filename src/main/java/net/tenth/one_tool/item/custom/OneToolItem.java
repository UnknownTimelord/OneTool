package net.tenth.one_tool.item.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.inventory.OneToolInventory;
import net.tenth.one_tool.screen.custom.OneToolScreenHandler;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.GetToolDataHelper;
import net.tenth.one_tool.util.MiscHelper;
import net.tenth.one_tool.util.UseOnBlockHelper;
import org.jspecify.annotations.NonNull;
import oshi.util.tuples.Triplet;

import java.util.function.Consumer;

public class OneToolItem extends Item {

    public OneToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient() || !(user.getMainHandStack().getItem() instanceof OneToolItem))
            return ActionResult.PASS;

        ItemStack tool = user.getMainHandStack();

        if (tool.get(ModDataComponentTypes.ONE_TOOL_INV) == null) {
            tool.set(
                    ModDataComponentTypes.ONE_TOOL_INV,
                    new OneToolInventory(tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE))
            );
        }

        user.openHandledScreen(new ExtendedScreenHandlerFactory<ItemStack>() {
            @Override
            public @NonNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new OneToolScreenHandler(syncId, playerInventory, tool);
            }

            @Override
            public Text getDisplayName() {
                return Text.translatable("screen.onetool.title");
            }

            @Override
            public ItemStack getScreenOpeningData(@NonNull ServerPlayerEntity serverPlayerEntity) {
                return tool;
            }
        });

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity user = context.getPlayer();
        ItemStack itemStack =
                user != null
                ? user.getMainHandStack()
                : ItemStack.EMPTY;

        Triplet<Boolean, BlockPos, Direction> raycast = UseOnBlockHelper.simpleRaycast(world, user, Fluids.EMPTY);
        if (raycast.getA()) return ActionResult.PASS;

        BlockPos aBlock = raycast.getB();
        Direction clickedFaceDirection = raycast.getC();
        BlockPos bBlock = aBlock.offset(clickedFaceDirection);
        if (!world.canEntityModifyAt(user, aBlock) || !user.canPlaceOn(bBlock, clickedFaceDirection, itemStack)) {
            return ActionResult.FAIL;
        }

        BlockState aBlockState = world.getBlockState(aBlock);

        if (aBlockState.isIn(BlockTags.LOGS)) return UseOnBlockHelper.axeUseOnBlock(context);
        else if (aBlockState.isIn(BlockTags.DIRT)
                && world.getBlockState(aBlock).getBlock() != Blocks.GRASS_BLOCK
                && world.getBlockState(aBlock).getBlock() != Blocks.PODZOL
                && world.getBlockState(aBlock).getBlock() != Blocks.MYCELIUM) return UseOnBlockHelper.hoeUseOnBlock(context);
        else return UseOnBlockHelper.shovelUseOnBlock(context);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        int energy = stack.getOrDefault(ModDataComponentTypes.ENERGY, 0);

        if (energy - 1 >= 0) {
            stack.set(ModDataComponentTypes.ENERGY, energy - 1);
        }

        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        int energy = stack.getOrDefault(ModDataComponentTypes.ENERGY, 0);

        if (energy - 1 >= 0) {
            stack.set(ModDataComponentTypes.ENERGY, energy - 1);
            super.postHit(stack, target, attacker);
        }

    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        int energy = damageSource.getAttacker() instanceof PlayerEntity playerEntity
                ? playerEntity.getMainHandStack().getOrDefault(ModDataComponentTypes.ENERGY, 0)
                : 0;
        return energy > 0
                ? super.getBonusAttackDamage(target, baseAttackDamage, damageSource)
                : -baseAttackDamage + 1F;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getMiningSpeed(stack, state);
        int energy = stack.getOrDefault(ModDataComponentTypes.ENERGY, 0);

        if (energy > 0) {
            return baseSpeed;
        }

        return !state.isIn(BlockTags.DIRT) ? baseSpeed * 0.1F : baseSpeed * 0.5F;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        int maxEnergy = GetToolDataHelper.getMaxEnergy(stack);
        int energy = GetToolDataHelper.getEnergy(stack);
        OneToolTier tier = GetToolDataHelper.getToolTier(stack);

        if (energy != 0) {
            textConsumer.accept(Text.translatable("one_tool.energy.tooltip")
                    .append(Text.translatable("one_tool.energy_value.tooltip", energy, maxEnergy)));
        }
        if (energy == 0) {
            long ms = Util.getMeasuringTimeMs();
            float t = (ms % 1200L) / 1200f;
            float pulse = 2F * (float) Math.sin(t * Math.PI);

            int color = MiscHelper.interpolateColor(Colors.BLACK, Colors.RED, pulse);

            textConsumer.accept(Text.translatable("one_tool.energy.tooltip")
                    .append(Text.translatable("one_tool.empty.tooltip")
                            .withColor(color)
                            .formatted(Formatting.BOLD)
                    )
            );

        }
        textConsumer.accept(Text.translatable("one_tool.tier.tooltip")
                .append(Text.translatable("one_tool.tier_value.tooltip", tier.asInt())));
    }
}

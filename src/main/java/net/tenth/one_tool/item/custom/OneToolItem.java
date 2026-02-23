package net.tenth.one_tool.item.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.inventory.OneToolInventory;
import net.tenth.one_tool.screen.custom.OneToolScreenHandler;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.Constants;
import net.tenth.one_tool.util.GetToolDataHelper;
import net.tenth.one_tool.util.MiscHelper;
import net.tenth.one_tool.util.UseOnBlockHelper;
import org.jspecify.annotations.NonNull;
import oshi.util.tuples.Triplet;

public class OneToolItem extends Item {

    public OneToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if(world.isClient() || !(player.getMainHandStack().getItem() instanceof OneToolItem))
            return ActionResult.PASS;

        ItemStack tool = player.getMainHandStack();

        if (player.isSneaking() && player.getHungerManager().isNotFull() && GetToolDataHelper.hasEnergy(tool)) {
            ConsumableComponent consumableComponent = tool.get(DataComponentTypes.CONSUMABLE);
            if (consumableComponent != null) {
                return consumableComponent.consume(player, tool.copy(), hand);
            }
        }

        if (tool.get(ModDataComponentTypes.ONE_TOOL_INV) == null) {
            tool.set(
                    ModDataComponentTypes.ONE_TOOL_INV,
                    new OneToolInventory(tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE))
            );
        }

        player.openHandledScreen(new ExtendedScreenHandlerFactory<ItemStack>() {
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
        PlayerEntity player = context.getPlayer();
        ItemStack tool =
                player != null
                        ? player.getMainHandStack()
                        : ItemStack.EMPTY;

        if (!GetToolDataHelper.hasEnergy(tool)) return ActionResult.FAIL;

        if (player == null || player.isSneaking() || tool == ItemStack.EMPTY || player.getHungerManager().isNotFull()) return ActionResult.PASS;

        Triplet<Boolean, BlockPos, Direction> raycast = UseOnBlockHelper.simpleRaycast(world, player, Fluids.EMPTY);
        if (raycast.getA()) return ActionResult.PASS;

        BlockPos aBlock = raycast.getB();
        Direction clickedFaceDirection = raycast.getC();
        BlockPos bBlock = aBlock.offset(clickedFaceDirection);
        if (!world.canEntityModifyAt(player, aBlock) || !player.canPlaceOn(bBlock, clickedFaceDirection, tool)) {
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
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ConsumableComponent cc = stack.get(DataComponentTypes.CONSUMABLE);
        if (cc != null && user instanceof PlayerEntity player) {
            int toFill = MiscHelper.getMissingHunger(player);
            stack.set(DataComponentTypes.FOOD, new FoodComponent(toFill, (float) toFill / 2, false));
            cc.finishConsumption(world, user, stack.copy());

            player.setComponent(ModDataComponentTypes.HAS_EATEN_ONE_TOOL, true);

            decrementEnergy(stack, toFill);
            stack.set(DataComponentTypes.FOOD, new FoodComponent(0, 0, false));
        }
        return stack.copy();
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        decrementEnergy(stack, 1);
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (decrementEnergy(stack, 1)) {
            super.postHit(stack, target, attacker);
        }
    }

    private static boolean decrementEnergy(ItemStack stack, int amount) {
        int tier = GetToolDataHelper.getToolTier(stack).asInt();
        int energy = stack.getOrDefault(ModDataComponentTypes.ENERGY, 0);

        if ((energy -= amount) < 0) return false;

        float consumeChance = switch (tier) {
            case 1 -> 0.10f;
            case 2 -> 0.20f;
            case 3 -> 0.30f;
            case 4 -> 0.40f;
            default -> 1.0f;
        };

        if (Constants.RANDOM.nextFloat() >= consumeChance) {
            stack.set(ModDataComponentTypes.ENERGY, energy);
            return true;
        }
        return false;
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
            return baseSpeed * GetToolDataHelper.getToolTier(stack).asInt() * GetToolDataHelper.getPreviousTier(stack).asInt();
        }

        return !state.isIn(BlockTags.DIRT) ? baseSpeed * 0.1F : baseSpeed * 0.5F;
    }
}

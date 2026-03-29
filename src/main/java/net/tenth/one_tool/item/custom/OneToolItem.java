package net.tenth.one_tool.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.inventory.OneToolInventory;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.Constants;
import net.tenth.one_tool.util.GetToolDataHelper;
import net.tenth.one_tool.util.MiscHelper;
import net.tenth.one_tool.util.UseOnBlockHelper;
import org.jspecify.annotations.Nullable;
import oshi.util.tuples.Triplet;

public class OneToolItem extends Item {

    public OneToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world.isClient()
                || !stack.isOf(this)
                || !stack.getOrDefault(ModDataComponentTypes.CONSUME, false)
                || !(entity instanceof PlayerEntity player)
                || player.getMainHandStack() != stack) return;

        int hunger = stack.getOrDefault(ModDataComponentTypes.HUNGER, 0);
        int cooldown = stack.getOrDefault(ModDataComponentTypes.CONSUME_COOLDOWN, 0);

        if (hunger < 20 && cooldown == 0) {
            OneToolTier tier = stack.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE);
            OneToolInventory toolInventory = stack.getOrDefault(ModDataComponentTypes.ONE_TOOL_INV, new OneToolInventory(tier));
            OneToolInventory unchangedInv = stack.getOrDefault(ModDataComponentTypes.ONE_TOOL_INV, new OneToolInventory(tier));
            for (ItemStack itemStack : toolInventory) {
                if (!itemStack.isOf(this) && itemStack.contains(DataComponentTypes.FOOD)) {
                    int toStore = hunger + itemStack.getOrDefault(DataComponentTypes.FOOD, Constants.EMPTY_FOOD).nutrition();
                    if (toStore == hunger)
                        continue;
                    itemStack.decrement(1);
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (toStore > 20) toStore = 20;
                    stack.set(ModDataComponentTypes.HUNGER, toStore);
                    stack.set(ModDataComponentTypes.CONSUME_COOLDOWN, 20);
                }
            }
            if (toolInventory != unchangedInv) {
                stack.set(ModDataComponentTypes.ONE_TOOL_INV, toolInventory);
            }

            hunger = stack.getOrDefault(ModDataComponentTypes.HUNGER, 0);
            cooldown = stack.getOrDefault(ModDataComponentTypes.CONSUME_COOLDOWN, 0);

            if (hunger < 20 && cooldown == 0) {
                PlayerInventory playerInventory = player.getInventory();

                for (ItemStack itemStack : playerInventory) {
                    if (!itemStack.isOf(this) && itemStack.contains(DataComponentTypes.FOOD)) {
                        int toStore = hunger + itemStack.getOrDefault(DataComponentTypes.FOOD, Constants.EMPTY_FOOD).nutrition();
                        if (toStore == hunger)
                            continue;
                        itemStack.decrement(1);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        if (toStore > 20) toStore = 20;
                        stack.set(ModDataComponentTypes.HUNGER, toStore);
                        stack.set(ModDataComponentTypes.CONSUME_COOLDOWN, 20);
                    }
                }
            }
        } else if (cooldown > 0) {
            stack.set(ModDataComponentTypes.CONSUME_COOLDOWN, cooldown - 1);
        }

        hunger = stack.getOrDefault(ModDataComponentTypes.HUNGER, 0);
        cooldown = stack.getOrDefault(ModDataComponentTypes.CONSUME_COOLDOWN, 0);

        if (cooldown == 0 && hunger >= 1 && (MiscHelper.getMissingHunger(player) >= 1 || player.getHungerManager().getSaturationLevel() < 20)) {
            if (MiscHelper.getMissingHunger(player) >= 1) {
                player.getHungerManager().add(1, 0);
            } else if (player.getHungerManager().getSaturationLevel() < 20 && player.getHealth() == player.getMaxHealth()) {
                player.getHungerManager().setSaturationLevel(player.getHungerManager().getSaturationLevel() + 1);
            }
            stack.set(ModDataComponentTypes.HUNGER, hunger - 1);
            stack.set(ModDataComponentTypes.CONSUME_COOLDOWN, 20);
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        int oldEnergy = GetToolDataHelper.getEnergy(oldStack);
        int newEnergy = GetToolDataHelper.getEnergy(newStack);
        int oldXp = GetToolDataHelper.getXP(oldStack);
        int newXp = GetToolDataHelper.getXP(newStack);
        return oldEnergy != newEnergy && oldXp != newXp;
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

        if (player == null || tool == ItemStack.EMPTY) return ActionResult.PASS;

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
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (decrementEnergy(stack, 1)) {
            OneToolTier tier = GetToolDataHelper.getToolTier(stack);
            if (tier == OneToolTier.QUADRUPLE) return super.postMine(stack, world, state, pos, miner);

            int xp = stack.getOrDefault(ModDataComponentTypes.XP, 0);
            int maxEnergy = GetToolDataHelper.getMaxEnergy(stack);
            if (xp == tier.asInt() * maxEnergy) {
                OneToolTier cur = stack.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE);
                OneToolTier next = cur.getNext();
                stack.set(ModDataComponentTypes.ONE_TOOL_TIER, next);
                stack.set(ModDataComponentTypes.XP, 0);
            } else {
                stack.set(ModDataComponentTypes.XP, xp + 1);
            }
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (decrementEnergy(stack, 1)) {
            OneToolTier tier = GetToolDataHelper.getToolTier(stack);

            if (tier == OneToolTier.QUADRUPLE) {
                super.postHit(stack, target, attacker);
                return;
            }

            int xp = stack.getOrDefault(ModDataComponentTypes.XP, 0);
            int maxEnergy = GetToolDataHelper.getMaxEnergy(stack);
            if (xp == tier.asInt() * maxEnergy) {
                OneToolTier cur = stack.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE);
                OneToolTier next = cur.getNext();
                stack.set(ModDataComponentTypes.ONE_TOOL_TIER, next);
                stack.set(ModDataComponentTypes.XP, 0);
            } else {
                stack.set(ModDataComponentTypes.XP, xp + 1);
            }
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

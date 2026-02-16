package net.tenth.one_tool.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import net.tenth.one_tool.component.ModDataComponentTypes;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.item.HoeItem.createTillAction;
import static net.minecraft.item.HoeItem.createTillAndDropAction;

public class UseOnBlockHelper {

    protected static final Map<Block, BlockState> PATH_STATES =
            new ImmutableMap.Builder<Block, BlockState>()
                    .put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.DIRT, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.PODZOL, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.MYCELIUM, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.getDefaultState())
                    .build();

    protected static final Map<Block, com.mojang.datafixers.util.Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS = Maps.newHashMap(
            ImmutableMap.of(
                    Blocks.GRASS_BLOCK,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.DIRT_PATH,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.DIRT,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.COARSE_DIRT,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.DIRT.getDefaultState())),
                    Blocks.ROOTED_DIRT,
                    com.mojang.datafixers.util.Pair.of(itemUsageContext -> true, createTillAndDropAction(Blocks.DIRT.getDefaultState(), Items.HANGING_ROOTS))
            )
    );

    protected static final Map<Block, Block> STRIPPED_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD)
            .put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
            .put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
            .put(Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD)
            .put(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG)
            .put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD)
            .put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
            .put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD)
            .put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG)
            .put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD)
            .put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
            .put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
            .put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
            .put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD)
            .put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
            .put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM)
            .put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
            .put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
            .put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE)
            .put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD)
            .put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG)
            .put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK)
            .build();

    protected static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
        Vec3d vec3d = player.getEyePos();
        Vec3d vec3d2 = vec3d.add(player.getRotationVector(player.getPitch(), player.getYaw()).multiply(player.getBlockInteractionRange()));
        return world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
    }

    public static Triplet<Boolean, BlockPos, Direction> simpleRaycast(World world, PlayerEntity user, Fluid tankFluid) {
        BlockHitResult blockHitResult = raycast(
                world, user, tankFluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE
        );

        return new Triplet<>(blockHitResult.getType() == HitResult.Type.MISS || blockHitResult.getType() != HitResult.Type.BLOCK,
                blockHitResult.getBlockPos(),
                blockHitResult.getSide());
    }

    // Shovel Stuff
    public static ActionResult shovelUseOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (context.getSide() == Direction.DOWN) {
            return ActionResult.PASS;
        } else {
            PlayerEntity playerEntity = context.getPlayer();
            BlockState blockState2 = PATH_STATES.get(blockState.getBlock());
            BlockState blockState3 = null;
            if (blockState2 != null && world.getBlockState(blockPos.up()).isAir()) {
                world.playSound(playerEntity, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                blockState3 = blockState2;
            } else if (blockState.getBlock() instanceof CampfireBlock && blockState.get(CampfireBlock.LIT)) {
                if (!world.isClient()) {
                    world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, blockPos, 0);
                }

                CampfireBlock.extinguish(context.getPlayer(), world, blockPos, blockState);
                blockState3 = blockState.with(CampfireBlock.LIT, false);
            }

            if (blockState3 != null) {
                if (!world.isClient()) {
                    world.setBlockState(blockPos, blockState3, Block.NOTIFY_ALL_AND_REDRAW);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState3));

                    ItemStack itemStack =
                            playerEntity != null
                                    ? playerEntity.getMainHandStack()
                                    : ItemStack.EMPTY;

                    int energy = itemStack.getOrDefault(ModDataComponentTypes.ENERGY, 0);

                    if (energy - 1 >= 0 && !playerEntity.isCreative()) {
                        itemStack.set(ModDataComponentTypes.ENERGY, energy - 1);
                    } else {
                        return ActionResult.FAIL;
                    }
                }

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        }
    }
    // Shovel Stuff

    // Hoe Stuff
    public static ActionResult hoeUseOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        com.mojang.datafixers.util.Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = TILLING_ACTIONS.get(
                world.getBlockState(blockPos).getBlock()
        );
        if (pair == null) {
            return ActionResult.PASS;
        } else {
            Predicate<ItemUsageContext> predicate = pair.getFirst();
            Consumer<ItemUsageContext> consumer = pair.getSecond();
            if (predicate.test(context)) {
                PlayerEntity playerEntity = context.getPlayer();
                world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClient()) {
                    consumer.accept(context);
                }

                ItemStack itemStack =
                        playerEntity != null
                                ? playerEntity.getMainHandStack()
                                : ItemStack.EMPTY;

                int energy = itemStack.getOrDefault(ModDataComponentTypes.ENERGY, 0);

                if (energy - 1 >= 0 && !playerEntity.isCreative()) {
                    itemStack.set(ModDataComponentTypes.ENERGY, energy - 1);
                } else {
                    return ActionResult.FAIL;
                }
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        }
    }
    // Hoe Stuff

    // Axe Stuff
    public static ActionResult axeUseOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();
        if (shouldCancelStripAttempt(context)) {
            return ActionResult.PASS;
        } else {
            Optional<BlockState> optional = tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos));
            if (optional.isEmpty()) {
                return ActionResult.PASS;
            } else {
                ItemStack itemStack = context.getStack();
                if (playerEntity instanceof ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                }

                world.setBlockState(blockPos, optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, optional.get()));
                int energy = itemStack.getOrDefault(ModDataComponentTypes.ENERGY, 0);

                if (energy - 1 >= 0 && !playerEntity.isCreative()) {
                    itemStack.set(ModDataComponentTypes.ENERGY, energy - 1);
                } else {
                    return ActionResult.FAIL;
                }

                return ActionResult.SUCCESS;
            }
        }
    }
    private static boolean shouldCancelStripAttempt(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        return context.getHand().equals(Hand.MAIN_HAND)
                && playerEntity.getOffHandStack().contains(DataComponentTypes.BLOCKS_ATTACKS)
                && !playerEntity.shouldCancelInteraction();
    }
    private static Optional<BlockState> tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
        Optional<BlockState> optional = getStrippedState(state);
        if (optional.isPresent()) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return optional;
        } else {
            Optional<BlockState> optional2 = Oxidizable.getDecreasedOxidationState(state);
            if (optional2.isPresent()) {
                world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.syncWorldEvent(player, WorldEvents.BLOCK_SCRAPED, pos, 0);
                return optional2;
            } else {
                Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap<?, ?>) HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(state.getBlock()))
                        .map(block -> block.getStateWithProperties(state));
                if (optional3.isPresent()) {
                    world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.syncWorldEvent(player, WorldEvents.WAX_REMOVED, pos, 0);
                    return optional3;
                } else {
                    return Optional.empty();
                }
            }
        }
    }
    private static Optional<BlockState> getStrippedState(BlockState state) {
        return Optional.ofNullable(STRIPPED_BLOCKS.get(state.getBlock()))
                .map(block -> block.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)));
    }
    // Axe Stuff

}

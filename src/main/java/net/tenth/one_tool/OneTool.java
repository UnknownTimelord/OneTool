package net.tenth.one_tool;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.tenth.one_tool.component.ModDataComponentTypes;
import net.tenth.one_tool.inventory.OneToolInventory;
import net.tenth.one_tool.item.ModItems;
import net.tenth.one_tool.item.custom.OneToolItem;
import net.tenth.one_tool.network.OpenOneToolInventoryC2SPacket;
import net.tenth.one_tool.network.TogglePickupC2SPacket;
import net.tenth.one_tool.screen.ModScreenHandlers;
import net.tenth.one_tool.screen.custom.OneToolScreenHandler;
import net.tenth.one_tool.types.OneToolTier;
import net.tenth.one_tool.util.GetToolDataHelper;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneTool implements ModInitializer {
	public static final String MOD_ID = "one_tool";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.init();
		ModDataComponentTypes.init();
		ModScreenHandlers.init();
		PayloadTypeRegistry.playC2S().register(TogglePickupC2SPacket.ID, TogglePickupC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(OpenOneToolInventoryC2SPacket.ID, OpenOneToolInventoryC2SPacket.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(TogglePickupC2SPacket.ID, (payload, context) -> {
			var player = context.player();
			var tool = context.player().getMainHandStack();

			context.server().execute(() -> {
				if (tool.getItem() instanceof OneToolItem) {
					boolean isActive = tool.getOrDefault(ModDataComponentTypes.PICKUP, false);
					if (isActive) {
						player.sendMessage(Text.translatable("one_tool.pickup_disabled"));
					} else {
						player.sendMessage(Text.translatable("one_tool.pickup_enabled"));
					}
					tool.set(ModDataComponentTypes.PICKUP, !isActive);
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(OpenOneToolInventoryC2SPacket.ID, (payload, context) -> {
			var player = context.player();
			var tool = context.player().getMainHandStack();
			context.server().execute(() -> {
				if (tool.getItem() instanceof OneToolItem) {
					OneToolInventory inventory = tool.get(ModDataComponentTypes.ONE_TOOL_INV);

					if (inventory == null) {
						tool.set(
								ModDataComponentTypes.ONE_TOOL_INV,
								new OneToolInventory(tool.getOrDefault(ModDataComponentTypes.ONE_TOOL_TIER, OneToolTier.BASE))
						);
					}
					if (inventory != null && inventory.size() < GetToolDataHelper.getMaxInvSize(tool)) {
						OneToolInventory newInv = new OneToolInventory(GetToolDataHelper.getMaxInvSize(tool), inventory.items);
						tool.set(ModDataComponentTypes.ONE_TOOL_INV, newInv);
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
				}
			});
		});
	}
}
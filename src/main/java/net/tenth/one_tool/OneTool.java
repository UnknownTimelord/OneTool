package net.tenth.one_tool;

import net.fabricmc.api.ModInitializer;

import net.tenth.one_tool.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneTool implements ModInitializer {
	public static final String MOD_ID = "one_tool";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.init();
	}
}
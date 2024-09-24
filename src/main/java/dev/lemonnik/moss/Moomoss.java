package dev.lemonnik.moss;

import dev.lemonnik.moss.entity.ModEntities;
import dev.lemonnik.moss.entity.custom.MoomossEntity;
import dev.lemonnik.moss.item.ModItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Moomoss implements ModInitializer {
	public static final String MOD_ID = "moss";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Adding Moomoss!");

		ModItems.registerModItems();
		FabricDefaultAttributeRegistry.register(ModEntities.MOOMOSS, MoomossEntity.setAttributes());
	}
}
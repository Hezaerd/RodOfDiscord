package com.hezaerd;

import com.hezaerd.registry.ModDataComponents;
import com.hezaerd.registry.ModItems;
import com.hezaerd.registry.ModLootTableModifiers;
import com.hezaerd.registry.ModStatusEffects;
import com.hezaerd.utils.ModLib;
import net.fabricmc.api.ModInitializer;

public class RodOfDiscord implements ModInitializer {
	
	@Override
	public void onInitialize() {
		ModLib.LOGGER.info("Initializing {}, version {}", ModLib.MOD_NAME, ModLib.getVersion());
		
		ModItems.init();
		ModDataComponents.init();
	 	ModStatusEffects.init();
		ModLootTableModifiers.init();
	}
}
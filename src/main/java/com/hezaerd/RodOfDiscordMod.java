package com.hezaerd;

import com.hezaerd.registry.ModEnchantmentEffects;
import com.hezaerd.registry.ModItems;
import com.hezaerd.registry.ModStatusEffects;
import net.fabricmc.api.ModInitializer;

public class RodOfDiscordMod implements ModInitializer {
	
	@Override
	public void onInitialize() {
		ModItems.init();
	 	ModStatusEffects.init();
		ModEnchantmentEffects.init();
	}
}
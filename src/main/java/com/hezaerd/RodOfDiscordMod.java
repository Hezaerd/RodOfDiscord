package com.hezaerd;

import com.hezaerd.item.RodOfDiscord;
import com.hezaerd.utils.ModLib;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class RodOfDiscordMod implements ModInitializer {

	public static Item ROD_OF_DISCORD;
	
	@Override
	public void onInitialize() {
		ROD_OF_DISCORD = registerRod("rod_of_discord", RodOfDiscord::new, new Item.Settings());
	}

	private static RodOfDiscord registerRod(String name, Function<Item.Settings, RodOfDiscord> itemFactory, Item.Settings settings) {
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, ModLib.id(name));
		RodOfDiscord item = itemFactory.apply(settings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return item;
	}
}
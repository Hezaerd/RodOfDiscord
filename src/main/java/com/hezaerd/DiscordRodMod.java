package com.hezaerd;

import com.hezaerd.item.DiscordRod;
import com.hezaerd.utils.ModLib;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class DiscordRodMod implements ModInitializer {

	public static Item DISCORD_ROD;
	
	@Override
	public void onInitialize() {
		DISCORD_ROD = registerRod("discord_rod", DiscordRod::new, new Item.Settings());
	}

	private static DiscordRod registerRod(String name, Function<Item.Settings, DiscordRod> itemFactory, Item.Settings settings) {
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, ModLib.id(name));
		DiscordRod item = itemFactory.apply(settings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return item;
	}
}
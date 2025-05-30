package com.hezaerd.registry;

import com.hezaerd.utils.ModLib;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModEnchantmentEffects {
    public static final RegistryKey<Enchantment> HARMONY = of("harmony");

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, ModLib.id(id));
    }
    
    public static void init() {

    }
}
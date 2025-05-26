package com.hezaerd.registry;

import com.hezaerd.enchantment.effect.DummyEffect;
import com.hezaerd.utils.ModLib;
import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModEnchantmentEffects {
    public static MapCodec<DummyEffect> DUMMY_EFFECT = register("dummy_effect", DummyEffect.CODEC);
    public static final RegistryKey<Enchantment> HARMONY = of("harmony");

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, ModLib.id(id));
    }

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, ModLib.id(id), codec);
    }

    public static void init() {
        
    }
}

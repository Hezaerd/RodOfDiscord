package com.hezaerd.registry;

import com.hezaerd.statuseffect.effect.ChaosEffect;
import com.hezaerd.utils.ModLib;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class ModStatusEffects {
    
    public static RegistryEntry<StatusEffect> CHAOS;

    public static void init() {
        CHAOS = register("chaos", new ChaosEffect(StatusEffectCategory.HARMFUL, 0xFF00FF));
    }
    
    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, ModLib.id(id), statusEffect);
    }
}

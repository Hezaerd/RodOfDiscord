package com.hezaerd.registry;

import com.hezaerd.utils.ModLib;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModDataComponents {
    
    public static final ComponentType<Boolean> HARMONIZED_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            ModLib.id("harmonized"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );
    
    public static void init() {
        
    }
}

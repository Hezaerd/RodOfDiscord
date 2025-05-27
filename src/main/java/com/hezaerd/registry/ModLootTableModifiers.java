package com.hezaerd.registry;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;

public class ModLootTableModifiers {
    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registry) -> {
            
            if(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.ROD_OF_DISCORD).weight(1));
                
                tableBuilder.pool(poolBuilder);
            }
        });
    }
}

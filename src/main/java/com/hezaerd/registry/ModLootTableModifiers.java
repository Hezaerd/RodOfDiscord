package com.hezaerd.registry;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModLootTableModifiers {
    private static final Identifier ENDER_DRAGON_ID = Identifier.ofVanilla("entities/ender_dragon");
    private static final Identifier WITHER_ID = Identifier.ofVanilla("entities/wither");
    private static final Identifier WARDEN_ID = Identifier.ofVanilla("entities/warden");
    
    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registry) -> {
            Identifier currentTableId = key.getValue();
            
            var enchantmentRegistry = registry.getOrThrow(RegistryKeys.ENCHANTMENT);
            var enchantmentHarmony = enchantmentRegistry.getOrThrow(ModEnchantmentEffects.HARMONY);
            
            if(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.ROD_OF_DISCORD).weight(1));
                
                tableBuilder.pool(poolBuilder);
            }
            
            if(ENDER_DRAGON_ID.equals(currentTableId) || WITHER_ID.equals(currentTableId) || WARDEN_ID.equals(currentTableId)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(WARDEN_ID.equals(currentTableId) ? 0.05f : 0.02f))
                        .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                                .apply(new SetEnchantmentsLootFunction.Builder()
                                        .enchantment(
                                                enchantmentHarmony,
                                                ConstantLootNumberProvider.create(1)
                                        )
                                )
                                .weight(1)
                        );

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}

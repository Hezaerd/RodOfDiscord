package com.hezaerd.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementsProvider extends FabricAdvancementProvider {

    public ModAdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registries, Consumer<AdvancementEntry> exporter) {
//        RegistryEntryLookup<EntityType<?>> entityRegistryLookup = registries.getOrThrow(RegistryKeys.ENTITY_TYPE);
//        
//        Advancement.Builder.create()
//                .display(
//                        Items.WARDEN_SPAWN_EGG,
//                        Text.literal("Kill the Warden"),
//                        Text.literal("Chase down the dominant creature of the Deep Dark in its own lair"),
//                        Identifier.of("textures/gui/advancements/backgrounds/adventure.png"),
//                        AdvancementFrame.CHALLENGE,
//                        true,
//                        true,
//                        false
//                )
//                .criterion("kill_warden", OnKilledCriterion.Conditions.createPlayerKilledEntity(
//                        EntityPredicate.Builder.create()
//                                .type(entityRegistryLookup, EntityType.WARDEN)
//                ))
//                .build(exporter, "rodofdiscord" + "/kill_warden");
    }
}

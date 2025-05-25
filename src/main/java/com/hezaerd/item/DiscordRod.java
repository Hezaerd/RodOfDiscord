package com.hezaerd.item;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class DiscordRod extends Item {
    public DiscordRod(Settings settings) {
        super(settings.maxCount(1).rarity(Rarity.UNCOMMON));
    }
}

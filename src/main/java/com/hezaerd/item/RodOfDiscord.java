package com.hezaerd.item;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class RodOfDiscord extends Item {
    public RodOfDiscord(Settings settings) {
        super(settings.maxCount(1).rarity(Rarity.UNCOMMON));
    }
}

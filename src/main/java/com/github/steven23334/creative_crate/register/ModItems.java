package com.github.steven23334.creative_crate.register;

import com.github.steven23334.creative_crate.CreativeCrateMod;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreativeCrateMod.MOD_ID);

    public static final DeferredItem<BlockItem> CreativeCrate = ITEMS.registerSimpleBlockItem(
            "creative_crate",
            ModBlocks.CREATIVE_CRATE
    );
}
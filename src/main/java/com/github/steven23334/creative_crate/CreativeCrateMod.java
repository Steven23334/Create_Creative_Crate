package com.github.steven23334.creative_crate;

import com.github.steven23334.creative_crate.register.ModBlockEntities;
import com.github.steven23334.creative_crate.register.ModBlocks;
import com.github.steven23334.creative_crate.register.ModItems;
import com.github.steven23334.steven_mod_api.ApiTabContributors;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.List;

@Mod(CreativeCrateMod.MOD_ID)
public class CreativeCrateMod {
    public static final String MOD_ID = "creative_crate";

    public CreativeCrateMod(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        ApiTabContributors.register(() -> List.of(
                new ItemStack(ModItems.CreativeCrate.get())
        ));
    }
}
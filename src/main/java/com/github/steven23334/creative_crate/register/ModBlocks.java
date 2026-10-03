package com.github.steven23334.creative_crate.register;

import com.github.steven23334.creative_crate.CreativeCrateMod;
import com.github.steven23334.creative_crate.block.CreativeCrateBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CreativeCrateMod.MOD_ID);

    public static final DeferredBlock<CreativeCrateBlock> CREATIVE_CRATE = BLOCKS.register(
            "creative_crate",
            () -> new CreativeCrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    );
}
package com.github.steven23334.creative_crate.register;

import com.github.steven23334.creative_crate.CreativeCrateMod;
import com.github.steven23334.creative_crate.block.CreativeCrateBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreativeCrateMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeCrateBlockEntity>> CREATIVE_CRATE_BE =
            BLOCK_ENTITIES.register("creative_crate", () ->
                    BlockEntityType.Builder
                            .of(CreativeCrateBlockEntity::new, ModBlocks.CREATIVE_CRATE.get())
                            .build(null)
            );
}
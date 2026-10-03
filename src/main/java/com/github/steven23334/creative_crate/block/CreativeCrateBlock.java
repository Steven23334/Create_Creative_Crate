package com.github.steven23334.creative_crate.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CreativeCrateBlock extends Block implements EntityBlock {

    // 14/16 = 0.875，贴底，x/z 居中
    private static final VoxelShape SHAPE = Shapes.box(
            0.0625, 0.0, 0.0625,
            0.9375, 0.875, 0.9375
    );

    public CreativeCrateBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeCrateBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CreativeCrateBlockEntity crate)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.isEmpty()) {
            // 空手右击：清除绑定
            if (!level.isClientSide) {
                crate.setProvidedStack(ItemStack.EMPTY);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // 手持物品右击：绑定物品
        if (!level.isClientSide) {
            ItemStack template = stack.copyWithCount(1);
            crate.setProvidedStack(template);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
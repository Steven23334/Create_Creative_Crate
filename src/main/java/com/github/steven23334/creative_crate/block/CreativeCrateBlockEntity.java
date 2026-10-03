package com.github.steven23334.creative_crate.block;

import com.github.steven23334.creative_crate.register.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeCrateBlockEntity extends BlockEntity implements Container {

    // 默认没有绑定
    private ItemStack providedStack = ItemStack.EMPTY;

    public CreativeCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CREATIVE_CRATE_BE.get(), pos, blockState);
    }

    public void setProvidedStack(ItemStack stack) {
        if (stack.isEmpty()) {
            this.providedStack = ItemStack.EMPTY;
        } else {
            // 去掉耐久组件，避免复制出带损耗的模板
//            copy.remove(net.minecraft.core.component.DataComponents.DAMAGE);
            this.providedStack = stack.copyWithCount(1);
        }
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasProvidedStack() {
        return !providedStack.isEmpty();
    }

    public ItemStack getProvidedStack() {
        if (providedStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = providedStack.copy();
        result.setCount(result.getMaxStackSize());
        return result;
    }

    public ItemStack getProvidedStackForDisplay() {
        return providedStack.copy();
    }

    // ---------- 同步 ----------

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ---------- Container 接口 ----------

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return providedStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return getProvidedStack();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack provided = getProvidedStack();
        provided.setCount(amount);
        return provided;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return getProvidedStack();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        // 忽略外部输入
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        // 不清理
    }

    // ---------- NBT ----------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // 空栈不能序列化，非空时才写入
        if (!providedStack.isEmpty()) {
            tag.put("ProvidedItem", providedStack.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("ProvidedItem")) {
            providedStack = ItemStack.parse(registries, tag.getCompound("ProvidedItem"))
                    .orElse(ItemStack.EMPTY);
        } else {
            providedStack = ItemStack.EMPTY;
        }
    }
}
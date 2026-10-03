package com.github.steven23334.creative_crate.client;

import com.github.steven23334.creative_crate.CreativeCrateMod;
import com.github.steven23334.creative_crate.block.CreativeCrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = CreativeCrateMod.MOD_ID, value = Dist.CLIENT)
public class CreativeCrateHudRenderer {

    private static final double FRAME_HALF = 0.1875;

    // 翻译键
    public static final String KEY_UNLIMITED_SUPPLY = "gui.creative_crate.unlimited_supply";
    public static final String KEY_CLICK_TO_REPLACE = "gui.creative_crate.click_to_replace";

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();

        if (!(mc.level.getBlockEntity(pos) instanceof CreativeCrateBlockEntity)) {
            return;
        }

        Vec3 loc = blockHit.getLocation();
        double localX = loc.x - pos.getX();
        double localY = loc.y - pos.getY();
        double localZ = loc.z - pos.getZ();

        if (localY < 0.87
                || Math.abs(localX - 0.5) > FRAME_HALF
                || Math.abs(localZ - 0.5) > FRAME_HALF) {
            return;
        }

        var gui = event.getGuiGraphics();
        Font font = mc.font;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int centerX = screenW / 2;
        int baseY = screenH / 2 + 40;

        Component line1 = Component.translatable(KEY_UNLIMITED_SUPPLY);
        Component line2 = Component.translatable(KEY_CLICK_TO_REPLACE);

        gui.drawCenteredString(font, line1, centerX, baseY, 0xFFFFFF);
        gui.drawCenteredString(font, line2, centerX, baseY + 12, 0xE0E0E0);
    }
}
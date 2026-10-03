package com.github.steven23334.creative_crate.client;

import com.github.steven23334.creative_crate.CreativeCrateMod;
import com.github.steven23334.creative_crate.block.CreativeCrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = CreativeCrateMod.MOD_ID, value = Dist.CLIENT)
public class CreativeCrateOverlayRenderer {

    private static final double FRAME_HALF = 0.1875;      // 3/8 / 2
    private static final double FRAME_THICKNESS = 0.0625; // 1/16
    private static final double GAP = 0.125;              // 1/8

    // 方块顶面高度：14/16 = 0.875
    private static final double TOP_Y = 0.875;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        BlockPos hitPos = null;
        Vec3 hitLocation = null;
        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            hitPos = blockHit.getBlockPos();
            hitLocation = blockHit.getLocation();
        }

        PoseStack ms = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        int radius = 8;
        BlockPos playerPos = mc.player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-radius, -radius, -radius),
                playerPos.offset(radius, radius, radius))) {

            BlockEntity be = mc.level.getBlockEntity(pos);
            if (!(be instanceof CreativeCrateBlockEntity crate)) {
                continue;
            }

            renderItem(mc, ms, buffer, camera, pos, crate.getProvidedStackForDisplay());

            if (pos.equals(hitPos) && isInsideFrame(hitLocation, pos)) {
                renderTopSquare(ms, buffer, camera, pos);
            }
        }

        buffer.endBatch();
    }

    private static boolean isInsideFrame(Vec3 hitLocation, BlockPos pos) {
        if (hitLocation == null) {
            return false;
        }

        double localX = hitLocation.x - pos.getX();
        double localZ = hitLocation.z - pos.getZ();
        double localY = hitLocation.y - pos.getY();

        // 顶面高度 0.875，留一点余量
        if (localY < 0.87) {
            return false;
        }

        return Math.abs(localX - 0.5) <= FRAME_HALF
                && Math.abs(localZ - 0.5) <= FRAME_HALF;
    }

    // ---------- 方形边框 ----------

    private static void renderTopSquare(PoseStack ms, MultiBufferSource buffer,
                                        Vec3 camera, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        double y  = pos.getY() + TOP_Y + 0.002; // 顶面上方 0.002

        double x0 = cx - FRAME_HALF;
        double x1 = cx + FRAME_HALF;
        double z0 = cz - FRAME_HALF;
        double z1 = cz + FRAME_HALF;
        double t  = FRAME_THICKNESS;

        double total = FRAME_HALF * 2;
        double segLen = (total - GAP) / 2;

        double sx0 = x0;
        double sx1 = x0 + segLen;
        double sx2 = x1 - segLen;
        double sx3 = x1;

        double sz0 = z0;
        double sz1 = z0 + segLen;
        double sz2 = z1 - segLen;
        double sz3 = z1;

        ms.pushPose();
        ms.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer consumer = buffer.getBuffer(RenderType.debugQuads());

        // 南边
        quad(ms, consumer, sx0, y, z0, sx1, y, z0 + t);
        quad(ms, consumer, sx2, y, z0, sx3, y, z0 + t);

        // 北边
        quad(ms, consumer, sx0, y, z1 - t, sx1, y, z1);
        quad(ms, consumer, sx2, y, z1 - t, sx3, y, z1);

        // 西边
        quad(ms, consumer, x0, y, sz0, x0 + t, y, sz1);
        quad(ms, consumer, x0, y, sz2, x0 + t, y, sz3);

        // 东边
        quad(ms, consumer, x1 - t, y, sz0, x1, y, sz1);
        quad(ms, consumer, x1 - t, y, sz2, x1, y, sz3);

        ms.popPose();
    }

    private static void quad(PoseStack ms, VertexConsumer consumer,
                             double x0, double y, double z0,
                             double x1, double y2, double z1) {
        float fx0 = (float) x0, fx1 = (float) x1;
        float fz0 = (float) z0, fz1 = (float) z1;
        float fy  = (float) y;

        consumer.addVertex(ms.last().pose(), fx0, fy, fz0).setColor(1f, 1f, 1f, 0.8f);
        consumer.addVertex(ms.last().pose(), fx0, fy, fz1).setColor(1f, 1f, 1f, 0.8f);
        consumer.addVertex(ms.last().pose(), fx1, fy, fz1).setColor(1f, 1f, 1f, 0.8f);
        consumer.addVertex(ms.last().pose(), fx1, fy, fz0).setColor(1f, 1f, 1f, 0.8f);
    }

    // ---------- 物品图标 ----------

    private static void renderItem(Minecraft mc, PoseStack ms, MultiBufferSource buffer,
                                   Vec3 camera, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = mc.getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, mc.level, null, 0);
        boolean blockItem = model.isGui3d();

        ms.pushPose();
        ms.translate(
                pos.getX() + 0.5 - camera.x,
                pos.getY() + TOP_Y + 0.03 - camera.y, // 顶面上方 0.03
                pos.getZ() + 0.5 - camera.z
        );

        ms.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));

        float scale = blockItem ? 0.5f : 0.25f;
        ms.scale(scale, scale, scale);

        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                0xF000F0,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                ms,
                buffer,
                mc.level,
                0
        );
        ms.popPose();
    }
}
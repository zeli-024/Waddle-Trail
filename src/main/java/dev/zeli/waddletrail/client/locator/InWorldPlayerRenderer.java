package dev.zeli.waddletrail.client.locator;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.zeli.waddletrail.locator.PlayerPosition;
import dev.zeli.waddletrail.network.ClientPositionCache;
import dev.zeli.waddletrail.network.LocatorSettings;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/** Renders server-authorized player heads above their world positions. */
public final class InWorldPlayerRenderer {
    public static final InWorldPlayerRenderer INSTANCE = new InWorldPlayerRenderer();
    private static final float MARKER_SCALE = 0.025F;
    private InWorldPlayerRenderer() {}

    @SubscribeEvent
    public void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientPositionCache.clear();
    }

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        LocatorSettings settings = ClientPositionCache.settings();
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || !settings.enabled()
                || !settings.showInWorld()) return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null) return;

        Camera camera = event.getCamera();
        Vec3 cameraPosition = camera.getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        for (PlayerPosition position : ClientPositionCache.snapshot()) {
            if (!position.online() || position.uuid().equals(minecraft.player.getUUID())
                    || !position.dimension().equals(level.dimension().location().toString())) continue;
            if (settings.requireTab() && (minecraft.getConnection() == null
                    || minecraft.getConnection().getPlayerInfo(position.uuid()) == null)) continue;

            Player loaded = findLoadedPlayer(level, position);
            Vec3 feet = loaded == null ? new Vec3(position.x(), position.y(), position.z()) : loaded.getPosition(partialTick);
            double height = loaded == null ? 1.8D : loaded.getBbHeight();
            Vec3 marker = feet.add(0.0D, height + 0.65D, 0.0D);
            double distance = cameraPosition.distanceTo(marker);
            if (distance > settings.inWorldDistance()) continue;

            boolean occluded = isOccluded(level, minecraft.player, cameraPosition, marker, feet.add(0.0D, height * 0.55D, 0.0D));
            boolean seeThrough = occluded && distance <= settings.throughWallDistance();
            if (occluded && !seeThrough) continue;

            ResourceLocation skin = loaded instanceof AbstractClientPlayer clientPlayer
                    ? clientPlayer.getSkin().texture()
                    : minecraft.getSkinManager().getInsecureSkin(new GameProfile(position.uuid(), position.name())).texture();
            renderMarker(event.getPoseStack(), buffers, minecraft, cameraPosition, marker, skin, position,
                    distance, partialTick, seeThrough, settings);
        }
        buffers.endBatch();
    }

    private Player findLoadedPlayer(ClientLevel level, PlayerPosition position) {
        for (Player player : level.players()) if (player.getUUID().equals(position.uuid())) return player;
        return null;
    }

    private boolean isOccluded(ClientLevel level, Player viewer, Vec3 from, Vec3 head, Vec3 chest) {
        return blocked(level, viewer, from, head) && blocked(level, viewer, from, chest);
    }

    private boolean blocked(ClientLevel level, Player viewer, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, viewer)).getType()
                != HitResult.Type.MISS;
    }

    private void renderMarker(PoseStack poseStack, MultiBufferSource.BufferSource buffers, Minecraft minecraft,
                              Vec3 camera, Vec3 marker, ResourceLocation skin, PlayerPosition position,
                              double distance, float partialTick, boolean seeThrough, LocatorSettings settings) {
        poseStack.pushPose();
        poseStack.translate(marker.x - camera.x, marker.y - camera.y, marker.z - camera.z);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(MARKER_SCALE, -MARKER_SCALE, MARKER_SCALE);
        Matrix4f matrix = poseStack.last().pose();
        RenderType renderType = seeThrough ? RenderType.textSeeThrough(skin) : RenderType.text(skin);
        VertexConsumer vertices = buffers.getBuffer(renderType);
        drawFace(vertices, matrix, 0.125F, 0.125F, 0.25F, 0.25F, 0.0F);
        drawFace(vertices, matrix, 0.625F, 0.125F, 0.75F, 0.25F, -0.01F);

        if (isDirectlyViewed(minecraft, marker, partialTick)) {
            String label = label(position.name(), distance, settings);
            if (!label.isEmpty()) {
                Font font = minecraft.font;
                Component text = Component.literal(label);
                float x = -font.width(text) / 2.0F;
                Font.DisplayMode mode = seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
                font.drawInBatch(text, x, 10.0F, 0xFFFFFFFF, false, matrix, buffers, mode, 0x40000000,
                        LightTexture.FULL_BRIGHT);
            }
        }
        poseStack.popPose();
    }

    private void drawFace(VertexConsumer vertices, Matrix4f matrix, float u0, float v0, float u1, float v1, float z) {
        vertices.addVertex(matrix, -8.0F, -8.0F, z).setColor(-1).setUv(u0, v0).setLight(LightTexture.FULL_BRIGHT);
        vertices.addVertex(matrix, -8.0F, 8.0F, z).setColor(-1).setUv(u0, v1).setLight(LightTexture.FULL_BRIGHT);
        vertices.addVertex(matrix, 8.0F, 8.0F, z).setColor(-1).setUv(u1, v1).setLight(LightTexture.FULL_BRIGHT);
        vertices.addVertex(matrix, 8.0F, -8.0F, z).setColor(-1).setUv(u1, v0).setLight(LightTexture.FULL_BRIGHT);
    }

    private boolean isDirectlyViewed(Minecraft minecraft, Vec3 marker, float partialTick) {
        Vec3 direction = marker.subtract(minecraft.player.getEyePosition(partialTick));
        return direction.lengthSqr() > 0.0D && minecraft.player.getViewVector(partialTick).dot(direction.normalize()) >= 0.9925D;
    }

    private String label(String name, double distance, LocatorSettings settings) {
        boolean showName = settings.showNames();
        boolean showDistance = settings.showDistance();
        if (showName && showDistance) return name + " · " + Math.round(distance) + "m";
        if (showName) return name;
        if (showDistance) return Math.round(distance) + "m";
        return "";
    }
}

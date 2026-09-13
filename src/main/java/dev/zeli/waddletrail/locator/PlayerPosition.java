package dev.zeli.waddletrail.locator;

import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

/** One authoritative server snapshot used by live and offline player markers. */
public record PlayerPosition(UUID uuid, String name, String dimension, double x, double y, double z,
                             float yaw, long lastSeenEpochMillis, boolean online) {
    public static PlayerPosition capture(ServerPlayer player, boolean online) {
        return new PlayerPosition(player.getUUID(), player.getGameProfile().getName(),
                player.serverLevel().dimension().location().toString(), player.getX(), player.getY(),
                player.getZ(), player.getYRot(), System.currentTimeMillis(), online);
    }

    public PlayerPosition offline(long timestamp) {
        return new PlayerPosition(uuid, name, dimension, x, y, z, yaw, timestamp, false);
    }
}

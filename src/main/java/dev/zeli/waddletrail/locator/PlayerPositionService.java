package dev.zeli.waddletrail.locator;

import dev.zeli.waddletrail.config.PlayerLocatorConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Tracks positions in memory and persists them without per-tick disk access. */
public final class PlayerPositionService {
    public static final PlayerPositionService INSTANCE = new PlayerPositionService();
    private static final int SAVE_INTERVAL_TICKS = 1200;
    private final Map<UUID, PlayerPosition> positions = new LinkedHashMap<>();
    private int positionTickCounter;
    private int saveTickCounter;
    private boolean dirty;
    private PlayerPositionService() {}

    public void start(MinecraftServer server) {
        positions.clear();
        positions.putAll(PlayerPositionStore.load());
        markAllOffline();
        pruneExpired();
        dirty = true;
        save();
    }

    public int trackedCount() { return positions.size(); }
    public Map<UUID, PlayerPosition> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(positions));
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) capture(player, true);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            capture(player, false);
            save();
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (!PlayerLocatorConfig.ENABLED.get()) return;
        positionTickCounter++;
        saveTickCounter++;
        if (positionTickCounter >= PlayerLocatorConfig.UPDATE_INTERVAL_TICKS.get()) {
            positionTickCounter = 0;
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) capture(player, true);
        }
        if (dirty && saveTickCounter >= SAVE_INTERVAL_TICKS) save();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        markAllOffline();
        save();
        positions.clear();
    }

    private void capture(ServerPlayer player, boolean online) {
        positions.put(player.getUUID(), PlayerPosition.capture(player, online));
        dirty = true;
    }

    private void markAllOffline() {
        long now = System.currentTimeMillis();
        positions.replaceAll((uuid, position) -> position.online() ? position.offline(now) : position);
    }

    private void pruneExpired() {
        int retentionDays = PlayerLocatorConfig.OFFLINE_RETENTION_DAYS.get();
        if (retentionDays == 0) return;
        long cutoff = System.currentTimeMillis() - retentionDays * 86_400_000L;
        positions.values().removeIf(position -> !position.online() && position.lastSeenEpochMillis() < cutoff);
    }

    private void save() {
        if (!dirty) return;
        PlayerPositionStore.save(positions);
        saveTickCounter = 0;
        dirty = false;
    }
}

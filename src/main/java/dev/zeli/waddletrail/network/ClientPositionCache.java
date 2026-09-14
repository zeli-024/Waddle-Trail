package dev.zeli.waddletrail.network;

import dev.zeli.waddletrail.locator.PlayerPosition;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Client cache populated only by authenticated server payloads. */
public final class ClientPositionCache {
    private static final Map<UUID, PlayerPosition> POSITIONS = new ConcurrentHashMap<>();
    private static volatile LocatorSettings settings = LocatorSettings.DISABLED;
    private ClientPositionCache() {}

    public static void handle(PlayerPositionsPayload payload, IPayloadContext context) {
        settings = payload.settings();
        if (payload.replace()) POSITIONS.clear();
        for (PlayerPosition position : payload.positions()) POSITIONS.put(position.uuid(), position);
    }

    public static PlayerPosition get(UUID uuid) { return POSITIONS.get(uuid); }
    public static List<PlayerPosition> snapshot() { return List.copyOf(POSITIONS.values()); }
    public static LocatorSettings settings() { return settings; }
    public static void clear() {
        POSITIONS.clear();
        settings = LocatorSettings.DISABLED;
    }
}

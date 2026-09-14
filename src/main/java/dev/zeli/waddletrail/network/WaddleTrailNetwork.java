package dev.zeli.waddletrail.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Versioned Waddle Trail play networking. */
public final class WaddleTrailNetwork {
    public static final String PROTOCOL_VERSION = "1";
    private WaddleTrailNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION).playToClient(
                PlayerPositionsPayload.TYPE,
                PlayerPositionsPayload.STREAM_CODEC,
                ClientPositionCache::handle
        );
    }
}

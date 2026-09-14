package dev.zeli.waddletrail.network;

import dev.zeli.waddletrail.WaddleTrail;
import dev.zeli.waddletrail.locator.PlayerPosition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** A bounded batch of authoritative server player positions. */
public record PlayerPositionsPayload(boolean replace, LocatorSettings settings, List<PlayerPosition> positions) implements CustomPacketPayload {
    public static final Type<PlayerPositionsPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(WaddleTrail.MOD_ID, "player_positions")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerPositionsPayload> STREAM_CODEC =
            StreamCodec.of(PlayerPositionsPayload::encode, PlayerPositionsPayload::decode);

    private static void encode(RegistryFriendlyByteBuf buffer, PlayerPositionsPayload payload) {
        buffer.writeBoolean(payload.replace);
        buffer.writeBoolean(payload.settings.enabled());
        buffer.writeBoolean(payload.settings.showInWorld());
        buffer.writeBoolean(payload.settings.showNames());
        buffer.writeBoolean(payload.settings.showDistance());
        buffer.writeBoolean(payload.settings.requireTab());
        buffer.writeVarInt(payload.settings.inWorldDistance());
        buffer.writeVarInt(payload.settings.throughWallDistance());
        buffer.writeVarInt(payload.positions.size());
        for (PlayerPosition position : payload.positions) {
            buffer.writeUUID(position.uuid());
            buffer.writeUtf(position.name(), 64);
            buffer.writeUtf(position.dimension(), 256);
            buffer.writeDouble(position.x());
            buffer.writeDouble(position.y());
            buffer.writeDouble(position.z());
            buffer.writeFloat(position.yaw());
            buffer.writeLong(position.lastSeenEpochMillis());
            buffer.writeBoolean(position.online());
        }
    }

    private static PlayerPositionsPayload decode(RegistryFriendlyByteBuf buffer) {
        boolean replace = buffer.readBoolean();
        LocatorSettings settings = new LocatorSettings(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
                buffer.readBoolean(), buffer.readBoolean(), buffer.readVarInt(), buffer.readVarInt());
        int size = buffer.readVarInt();
        if (size < 0 || size > 10_000) throw new IllegalArgumentException("Invalid position count: " + size);
        List<PlayerPosition> positions = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            UUID uuid = buffer.readUUID();
            String name = buffer.readUtf(64);
            String dimension = buffer.readUtf(256);
            double x = buffer.readDouble();
            double y = buffer.readDouble();
            double z = buffer.readDouble();
            float yaw = buffer.readFloat();
            long lastSeen = buffer.readLong();
            boolean online = buffer.readBoolean();
            positions.add(new PlayerPosition(uuid, name, dimension, x, y, z, yaw, lastSeen, online));
        }
        return new PlayerPositionsPayload(replace, settings, List.copyOf(positions));
    }

    public static PlayerPositionsPayload fromServer(boolean replace, List<PlayerPosition> positions) {
        return new PlayerPositionsPayload(replace, LocatorSettings.fromServerConfig(), positions);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

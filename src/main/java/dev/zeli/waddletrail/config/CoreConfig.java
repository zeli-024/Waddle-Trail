package dev.zeli.waddletrail.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CoreConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue DEBUG_LOGGING;
    public static final ModConfigSpec.BooleanValue SHOW_SYNC_STATUS;
    public static final ModConfigSpec.BooleanValue PRIVATE_SYNC_ENABLED;
    public static final ModConfigSpec.IntValue SYNC_INTERVAL_MINUTES;
    public static final ModConfigSpec.IntValue TRANSFER_LIMIT_KBPS;
    public static final ModConfigSpec.BooleanValue BACKUP_WAYPOINTS;
    public static final ModConfigSpec.IntValue MAX_STORAGE_PER_PLAYER_MB;
    public static final ModConfigSpec.BooleanValue COMMUNAL_SYNC_ENABLED;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("general");
        DEBUG_LOGGING = BUILDER.comment("Writes detailed diagnostic information to the log.").define("debugLogging", false);
        SHOW_SYNC_STATUS = BUILDER.comment("Shows short messages when map synchronization begins or finishes.").define("showSyncStatus", true);
        BUILDER.pop();
        BUILDER.push("map_sync");
        PRIVATE_SYNC_ENABLED = BUILDER.comment("Stores each player's Xaero map separately under their authenticated UUID.").define("privateSyncEnabled", true);
        SYNC_INTERVAL_MINUTES = BUILDER.comment("Minutes between background private-map synchronization attempts.").defineInRange("syncIntervalMinutes", 10, 1, 1440);
        TRANSFER_LIMIT_KBPS = BUILDER.comment("Maximum map transfer speed per connection in KB/s. Use 0 for unlimited.").defineInRange("transferLimitKBps", 128, 0, 10240);
        BACKUP_WAYPOINTS = BUILDER.comment("Includes Xaero waypoints and deathpoints in the private backup.").define("backupWaypoints", true);
        MAX_STORAGE_PER_PLAYER_MB = BUILDER.comment("Maximum server storage for one player's private map data.").defineInRange("maximumStoragePerPlayerMB", 1024, 64, 102400);
        COMMUNAL_SYNC_ENABLED = BUILDER.comment("Merges discoveries into one shared map and distributes changes to players.").define("communalSyncEnabled", false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private CoreConfig() {}
}

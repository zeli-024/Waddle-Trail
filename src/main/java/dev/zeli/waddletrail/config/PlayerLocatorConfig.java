package dev.zeli.waddletrail.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PlayerLocatorConfig {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue SHOW_ON_MINIMAP;
    public static final ModConfigSpec.BooleanValue SHOW_ON_WORLD_MAP;
    public static final ModConfigSpec.BooleanValue SHOW_IN_WORLD;
    public static final ModConfigSpec.BooleanValue SHOW_NAMES;
    public static final ModConfigSpec.BooleanValue SHOW_DISTANCE;
    public static final ModConfigSpec.BooleanValue REQUIRE_TAB;
    public static final ModConfigSpec.IntValue IN_WORLD_DISTANCE;
    public static final ModConfigSpec.IntValue THROUGH_WALL_DISTANCE;
    public static final ModConfigSpec.IntValue UPDATE_INTERVAL_TICKS;
    public static final ModConfigSpec.BooleanValue SHOW_OFFLINE;
    public static final ModConfigSpec.IntValue OFFLINE_RETENTION_DAYS;
    public static final ModConfigSpec.BooleanValue OPERATOR_TELEPORT;
    public static final ModConfigSpec SPEC;

    static {
        B.push("player_locator");
        ENABLED = B.comment("Enables Waddle Trail's player locator.").define("enabled", true);
        SHOW_ON_MINIMAP = B.comment("Shows online player heads on the minimap.").define("showOnlineOnMinimap", true);
        SHOW_ON_WORLD_MAP = B.comment("Shows online player heads on the fullscreen map.").define("showOnlineOnWorldMap", true);
        SHOW_IN_WORLD = B.comment("Shows online player heads above players in-world.").define("showOnlineInWorld", true);
        SHOW_NAMES = B.comment("Shows the name below an in-world head when directly viewed.").define("showInWorldNames", true);
        SHOW_DISTANCE = B.comment("Shows distance beside the in-world name.").define("showInWorldDistance", true);
        REQUIRE_TAB = B.comment("Requires the player to appear in the server player list.").define("requireTabInWorld", true);
        IN_WORLD_DISTANCE = B.comment("Maximum in-world head distance in blocks.").defineInRange("inWorldDistance", 64, 1, 2048);
        THROUGH_WALL_DISTANCE = B.comment("Through-wall distance. Use 0 for immediate occlusion.").defineInRange("throughWallDistance", 0, 0, 2048);
        UPDATE_INTERVAL_TICKS = B.comment("Ticks between position updates. 20 ticks equals one second.").defineInRange("positionUpdateIntervalTicks", 10, 1, 1200);
        SHOW_OFFLINE = B.comment("Shows offline players only on the fullscreen map.").define("showOfflineOnWorldMap", true);
        OFFLINE_RETENTION_DAYS = B.comment("Days to retain last positions. Use 0 indefinitely.").defineInRange("offlineRetentionDays", 30, 0, 36500);
        OPERATOR_TELEPORT = B.comment("Allows permission-level-2 operators to use /wt tp.").define("operatorTeleportEnabled", true);
        B.pop();
        SPEC = B.build();
    }

    private PlayerLocatorConfig() {}
}

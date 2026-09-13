package dev.zeli.waddletrail.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class FairPlayConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue MINIMAP_ENABLED;
    public static final ModConfigSpec.BooleanValue ALLOW_CAVE_MAP;
    public static final ModConfigSpec.BooleanValue ALLOW_ENTITY_RADAR;
    public static final ModConfigSpec.BooleanValue ALLOW_WAYPOINTS;
    public static final ModConfigSpec.BooleanValue SHOW_WAYPOINTS_IN_WORLD;
    public static final ModConfigSpec.BooleanValue ALLOW_DEATHPOINTS;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("fair_play");
        ENABLED = BUILDER.comment("Applies the server-controlled Xaero profile named 'Waddle Trail' to every player.").define("enabled", true);
        MINIMAP_ENABLED = BUILDER.comment("Allows Xaero's minimap HUD. The fullscreen map remains available.").define("minimapEnabled", true);
        ALLOW_CAVE_MAP = BUILDER.comment("Allows underground cave-map rendering.").define("allowCaveMap", false);
        ALLOW_ENTITY_RADAR = BUILDER.comment("Allows mobs and other entities on the minimap.").define("allowEntityRadar", false);
        ALLOW_WAYPOINTS = BUILDER.comment("Allows ordinary waypoints.").define("allowWaypoints", true);
        SHOW_WAYPOINTS_IN_WORLD = BUILDER.comment("Renders waypoints in the 3D world. This does not affect player heads.").define("showWaypointsInWorld", false);
        ALLOW_DEATHPOINTS = BUILDER.comment("Allows automatic deathpoints.").define("allowDeathpoints", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private FairPlayConfig() {}
}

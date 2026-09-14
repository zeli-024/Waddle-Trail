package dev.zeli.waddletrail.network;

import dev.zeli.waddletrail.config.PlayerLocatorConfig;

/** Server-controlled locator display settings sent with position snapshots. */
public record LocatorSettings(boolean enabled, boolean showInWorld, boolean showNames, boolean showDistance,
                              boolean requireTab, int inWorldDistance, int throughWallDistance) {
    public static final LocatorSettings DISABLED = new LocatorSettings(false, false, false, false, true, 64, 0);

    public static LocatorSettings fromServerConfig() {
        return new LocatorSettings(PlayerLocatorConfig.ENABLED.get(), PlayerLocatorConfig.SHOW_IN_WORLD.get(),
                PlayerLocatorConfig.SHOW_NAMES.get(), PlayerLocatorConfig.SHOW_DISTANCE.get(),
                PlayerLocatorConfig.REQUIRE_TAB.get(), PlayerLocatorConfig.IN_WORLD_DISTANCE.get(),
                PlayerLocatorConfig.THROUGH_WALL_DISTANCE.get());
    }
}

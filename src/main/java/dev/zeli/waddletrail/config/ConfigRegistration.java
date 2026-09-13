package dev.zeli.waddletrail.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public final class ConfigRegistration {
    private ConfigRegistration() {}

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CoreConfig.SPEC, "waddletrail/waddletrail.toml");
        container.registerConfig(ModConfig.Type.COMMON, FairPlayConfig.SPEC, "waddletrail/fair-play.toml");
        container.registerConfig(ModConfig.Type.COMMON, PlayerLocatorConfig.SPEC, "waddletrail/player-locator.toml");
    }
}

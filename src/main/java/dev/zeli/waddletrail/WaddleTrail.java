package dev.zeli.waddletrail;

import com.mojang.logging.LogUtils;
import dev.zeli.waddletrail.config.ConfigRegistration;
import dev.zeli.waddletrail.command.WaddleTrailCommands;
import dev.zeli.waddletrail.locator.PlayerPositionService;
import dev.zeli.waddletrail.storage.StorageLayout;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(WaddleTrail.MOD_ID)
public final class WaddleTrail {
    public static final String MOD_ID = "waddletrail";
    public static final String DISPLAY_NAME = "Waddle Trail - Xaero's Utils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WaddleTrail(IEventBus modBus, ModContainer container) {
        ConfigRegistration.register(container);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(PlayerPositionService.INSTANCE);
        NeoForge.EVENT_BUS.register(WaddleTrailCommands.INSTANCE);
        LOGGER.info("Loading {}", DISPLAY_NAME);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        StorageLayout.createServerDirectories();
        PlayerPositionService.INSTANCE.start(event.getServer());
    }
}

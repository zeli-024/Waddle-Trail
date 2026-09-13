package dev.zeli.waddletrail.storage;

import dev.zeli.waddletrail.WaddleTrail;
import net.neoforged.fml.loading.FMLPaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StorageLayout {
    public static final Path ROOT = FMLPaths.CONFIGDIR.get().resolve("waddletrail");
    public static final Path DATA = ROOT.resolve("data");
    public static final Path PRIVATE_MAPS = DATA.resolve("private");
    public static final Path COMMUNAL_MAP = DATA.resolve("communal");
    public static final Path PLAYER_POSITIONS = DATA.resolve("player-positions.json");
    private StorageLayout() {}

    public static void createServerDirectories() {
        try {
            Files.createDirectories(PRIVATE_MAPS);
            Files.createDirectories(COMMUNAL_MAP);
        } catch (IOException exception) {
            WaddleTrail.LOGGER.error("Could not create Waddle Trail storage directories", exception);
        }
    }
}

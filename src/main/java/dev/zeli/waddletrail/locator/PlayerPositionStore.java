package dev.zeli.waddletrail.locator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dev.zeli.waddletrail.WaddleTrail;
import dev.zeli.waddletrail.storage.StorageLayout;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Reads and atomically replaces player-positions.json. */
public final class PlayerPositionStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type FILE_TYPE = new TypeToken<Map<UUID, PlayerPosition>>() {}.getType();
    private PlayerPositionStore() {}

    public static Map<UUID, PlayerPosition> load() {
        Path file = StorageLayout.PLAYER_POSITIONS;
        if (Files.notExists(file)) return new LinkedHashMap<>();
        try (Reader reader = Files.newBufferedReader(file)) {
            Map<UUID, PlayerPosition> loaded = GSON.fromJson(reader, FILE_TYPE);
            return loaded == null ? new LinkedHashMap<>() : new LinkedHashMap<>(loaded);
        } catch (IOException | JsonParseException exception) {
            WaddleTrail.LOGGER.error("Could not read {}. Starting with an empty position cache.", file, exception);
            return new LinkedHashMap<>();
        }
    }

    public static void save(Map<UUID, PlayerPosition> positions) {
        Path file = StorageLayout.PLAYER_POSITIONS;
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(positions, FILE_TYPE, writer);
            }
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            WaddleTrail.LOGGER.error("Could not save {}", file, exception);
        }
    }
}

package dev.zeli.waddletrail.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.zeli.waddletrail.WaddleTrail;
import dev.zeli.waddletrail.config.CoreConfig;
import dev.zeli.waddletrail.config.FairPlayConfig;
import dev.zeli.waddletrail.config.PlayerLocatorConfig;
import dev.zeli.waddletrail.locator.PlayerPositionService;
import dev.zeli.waddletrail.storage.StorageLayout;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class WaddleTrailCommands {
    public static final WaddleTrailCommands INSTANCE = new WaddleTrailCommands();
    private WaddleTrailCommands() {}

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(command("wt"));
        event.getDispatcher().register(command("waddletrail"));
    }

    private LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .executes(context -> showStatus(context.getSource()))
                .then(Commands.literal("status").executes(context -> showStatus(context.getSource())));
    }

    private int showStatus(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal(WaddleTrail.DISPLAY_NAME + " 0.0.2"), false);
        source.sendSuccess(() -> Component.literal("Private sync: " + state(CoreConfig.PRIVATE_SYNC_ENABLED.get())), false);
        source.sendSuccess(() -> Component.literal("Communal sync: " + state(CoreConfig.COMMUNAL_SYNC_ENABLED.get())), false);
        source.sendSuccess(() -> Component.literal("Fair play: " + state(FairPlayConfig.ENABLED.get())), false);
        source.sendSuccess(() -> Component.literal("Player locator: " + state(PlayerLocatorConfig.ENABLED.get())
                + " | Stored players: " + PlayerPositionService.INSTANCE.trackedCount()), false);
        source.sendSuccess(() -> Component.literal("Storage: " + StorageLayout.DATA.toAbsolutePath()), false);
        return 1;
    }

    private String state(boolean enabled) { return enabled ? "enabled" : "disabled"; }
}

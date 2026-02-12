package dev.hytalemodding.authfree.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;
import dev.hytalemodding.authfree.AuthFreePlugin;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class RefreshCommand extends ConsoleOnlyCommand {

    private final AuthFreePlugin plugin;

    public RefreshCommand(@Nonnull AuthFreePlugin plugin) {
        super("refresh", "Force refresh of authentication tokens");
        this.plugin = plugin;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeConsoleAsync(@Nonnull CommandContext context) {
        context.sender().sendMessage(
                Message.raw("Refreshing tokens...").color(Color.YELLOW)
        );
        boolean success = plugin.refreshTokens();
        if (success) {
            context.sender().sendMessage(
                    Message.raw("Token refresh successful.")
                            .color(Color.GREEN)
            );
        } else {
            context.sender().sendMessage(
                    Message.raw("Token refresh failed.")
                            .color(Color.RED)
            );
        }
        return CompletableFuture.completedFuture(null);
    }
}
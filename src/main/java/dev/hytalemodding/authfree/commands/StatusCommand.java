package dev.hytalemodding.authfree.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;
import dev.hytalemodding.authfree.AuthFreePlugin;
import dev.hytalemodding.authfree.AuthFreeData;
import dev.hytalemodding.authfree.config.AuthFreeConfig;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class StatusCommand extends ConsoleOnlyCommand {

    private final AuthFreePlugin plugin;

    public StatusCommand(@Nonnull AuthFreePlugin plugin) {
        super("status", "Show current authentication status");
        this.plugin = plugin;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeConsoleAsync(@Nonnull CommandContext context) {
        AuthFreeConfig config = plugin.getConfig();
        AuthFreeData data = plugin.getTokenData();

        context.sender().sendMessage(
                Message.raw("Fetching status...")
                        .color(Color.YELLOW)
                        .bold(true)
        );
        context.sender().sendMessage(
                Message.raw("Auth server: " + config.getAuthServer()).color(Color.WHITE)
        );
        context.sender().sendMessage(
                Message.raw("Server name: " + config.getServerName()).color(Color.WHITE)
        );
        context.sender().sendMessage(
                Message.raw("Server ID: " + config.getServerId()).color(Color.WHITE)
        );
        context.sender().sendMessage(
                Message.raw("Auth on startup: " + config.isAuthOnStartup()).color(Color.WHITE)
        );
        context.sender().sendMessage(Message.raw("Refresh interval: " + config.getRefreshIntervalHours() + " hours").color(Color.WHITE)
        );

        if (data.isTokenPresent()) {
            long ageSeconds = (System.currentTimeMillis() - data.getLastRefresh()) / 1000;
            long expiresInSeconds = (data.getExpiresAt() - System.currentTimeMillis()) / 1000;
            context.sender().sendMessage(
                    Message.raw("Session token: present (last refresh " + ageSeconds + "s ago)")
                            .color(Color.GREEN)
            );
            if (expiresInSeconds > 0) {
                context.sender().sendMessage(
                        Message.raw("Token expires in: " + expiresInSeconds + "s").color(Color.GREEN)
                );
            } else {
                context.sender().sendMessage(
                        Message.raw("Token expired! Use /auth_free refresh.").color(Color.RED)
                );
            }
        } else {
            context.sender().sendMessage(
                    Message.raw("No authentication token stored.").color(Color.RED)
            );
            context.sender().sendMessage(
                    Message.raw("Use /auth_free login to obtain tokens.")
                            .color(Color.WHITE).italic(true)
            );
        }
        return CompletableFuture.completedFuture(null);
    }
}
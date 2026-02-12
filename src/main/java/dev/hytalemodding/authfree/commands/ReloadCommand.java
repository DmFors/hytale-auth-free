package dev.hytalemodding.authfree.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;
import dev.hytalemodding.authfree.AuthFreePlugin;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class ReloadCommand extends ConsoleOnlyCommand {

    private final AuthFreePlugin plugin;

    public ReloadCommand(@Nonnull AuthFreePlugin plugin) {
        super("reload", "Reload configuration from disk");
        this.plugin = plugin;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeConsoleAsync(@Nonnull CommandContext context) {
        context.sender().sendMessage(
                Message.raw("Reloading configuration...").color(Color.YELLOW)
        );
        plugin.reloadConfig();
        context.sender().sendMessage(
                Message.raw("Configuration reloaded.").color(Color.GREEN)
        );
        return CompletableFuture.completedFuture(null);
    }
}
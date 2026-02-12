package dev.hytalemodding.authfree.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;
import dev.hytalemodding.authfree.AuthFreePlugin;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class LoginCommand extends ConsoleOnlyCommand {

    private final AuthFreePlugin plugin;

    public LoginCommand(@Nonnull AuthFreePlugin plugin) {
        super("login", "Manually authenticate and obtain new tokens");
        this.plugin = plugin;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeConsoleAsync(@Nonnull CommandContext context) {
        context.sender().sendMessage(
                Message.raw("Performing manual login...").color(Color.YELLOW)
        );
        boolean success = plugin.performLogin(true);
        if (success) {
            context.sender().sendMessage(
                    Message.raw("Login successful. Tokens obtained and injected.")
                            .color(Color.GREEN)
            );
        } else {
            context.sender().sendMessage(
                    Message.raw("Login failed. Check server logs for details.")
                            .color(Color.RED)
            );
        }
        return CompletableFuture.completedFuture(null);
    }
}
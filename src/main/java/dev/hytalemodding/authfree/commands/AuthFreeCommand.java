package dev.hytalemodding.authfree.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.hytalemodding.authfree.AuthFreePlugin;

import javax.annotation.Nonnull;

public class AuthFreeCommand extends AbstractCommandCollection {

    public AuthFreeCommand(@Nonnull AuthFreePlugin plugin) {
        super("auth_free", "Manage F2P authentication for the server");
        addSubCommand(new LoginCommand(plugin));
        addSubCommand(new RefreshCommand(plugin));
        addSubCommand(new StatusCommand(plugin));
        addSubCommand(new ReloadCommand(plugin));
    }
}


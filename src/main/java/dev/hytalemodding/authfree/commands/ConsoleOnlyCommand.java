package dev.hytalemodding.authfree.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * Базовый класс для команд, которые могут выполняться только из консоли.
 * Автоматически проверяет отправителя и отклоняет запросы от игроков.
 */
public abstract class ConsoleOnlyCommand extends AbstractAsyncCommand {

    public ConsoleOnlyCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    @Override
    @Nonnull
    public final CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        return executeConsoleAsync(context);
    }

    /**
     * Реализация команды для консоли.
     */
    @Nonnull
    protected abstract CompletableFuture<Void> executeConsoleAsync(@Nonnull CommandContext context);
}
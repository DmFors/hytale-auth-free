package dev.hytalemodding.authfree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.auth.DualServerTokenManager;
import dev.hytalemodding.authfree.commands.AuthFreeCommand;
import dev.hytalemodding.authfree.config.AuthFreeConfig;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class AuthFreePlugin extends JavaPlugin {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Path dataFolder;
    private Path configFile;
    private Path dataFile;

    private AuthFreeConfig config;
    private AuthFreeData tokenData;
    private AuthClient authClient;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> refreshTask;

    public AuthFreePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("AuthFree initializing...");

        // 1. Создаём папку для данных мода
        this.dataFolder = Paths.get(System.getProperty("user.dir"), "mods", "AuthFree");
        this.configFile = dataFolder.resolve("auth_free_settings.json");
        this.dataFile = dataFolder.resolve("auth_free_data.json");

        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            getLogger().at(Level.SEVERE).log("Failed to create data directory: " + e.getMessage());
        }

        // 2. Загружаем конфигурацию (создаём дефолтную при необходимости)
        this.config = loadConfig();

        // 3. Загружаем сохранённые токены
        this.tokenData = loadData();

        // 4. Инициализируем HTTP-клиент
        this.authClient = new AuthClient(getLogger());

        // 5. Автоматический логин, если включено в конфиге
        if (config.isAuthOnStartup()) {
            performLogin(false);
        } else {
            getLogger().at(Level.INFO).log("Auto-login is disabled. Use /auth_free login to authenticate.");
        }

        // 6. Регистрируем команду
        getCommandRegistry().registerCommand(new AuthFreeCommand(this));

        // 7. Запускаем планировщик автоматического обновления токена, если интервал > 0
        if (config.getRefreshIntervalHours() > 0) {
            startRefreshScheduler();
        }

        getLogger().at(Level.INFO).log("AuthFree setup complete.");
    }

    // --- Загрузка/сохранение конфигурации ---
    private AuthFreeConfig loadConfig() {
        if (Files.exists(configFile)) {
            try {
                String json = Files.readString(configFile);
                AuthFreeConfig cfg = GSON.fromJson(json, AuthFreeConfig.class);
                cfg.generateServerIdIfMissing();
                saveConfig(cfg); // сохраняем, если сгенерировали новый ID
                return cfg;
            } catch (Exception e) {
                getLogger().at(Level.SEVERE).log("Failed to load config, using defaults: " + e.getMessage());
            }
        }
        // Создаём конфиг по умолчанию
        AuthFreeConfig defaultConfig = new AuthFreeConfig();
        defaultConfig.generateServerIdIfMissing();
        saveConfig(defaultConfig);
        return defaultConfig;
    }

    private void saveConfig(AuthFreeConfig config) {
        try {
            String json = GSON.toJson(config);
            Files.writeString(configFile, json);
        } catch (IOException e) {
            getLogger().at(Level.SEVERE).log("Failed to save config: " + e.getMessage());
        }
    }

    // --- Загрузка/сохранение токенов ---
    private AuthFreeData loadData() {
        if (Files.exists(dataFile)) {
            try {
                String json = Files.readString(dataFile);
                return GSON.fromJson(json, AuthFreeData.class);
            } catch (Exception e) {
                getLogger().at(Level.WARNING).log("Failed to load token data, starting fresh: " + e.getMessage());
            }
        }
        return new AuthFreeData();
    }

    private void saveData() {
        try {
            String json = GSON.toJson(tokenData);
            Files.writeString(dataFile, json);
        } catch (IOException e) {
            getLogger().at(Level.SEVERE).log("Failed to save token data: " + e.getMessage());
        }
    }

    public boolean performLogin(boolean force) {
        if (!force && tokenData.hasValidToken()) {
            getLogger().at(Level.INFO).log("Valid token already exists, skipping login. Use /auth_free refresh to force.");
            return true;
        }

        getLogger().at(Level.INFO).log("Requesting new authentication tokens...");
        AuthClient.TokenResponse response = authClient.requestTokens(
                config.getAuthServer(),
                config.getServerId(),
                config.getServerName()
        );

        if (response != null) {
            tokenData.setSessionToken(response.sessionToken());
            tokenData.setIdentityToken(response.identityToken());
            tokenData.setLastRefresh(System.currentTimeMillis());
            // Если сервер не возвращает expires_in, ставим +1 час (в соответствии с github sanasol)
            tokenData.setExpiresAt(System.currentTimeMillis() + 3600000L);
            saveData();

            // Передаём токены в патченный DualServerTokenManager
            DualServerTokenManager.setF2PTokens(response.sessionToken(), response.identityToken());
            getLogger().at(Level.INFO).log("Authentication successful. Tokens injected into DualServerTokenManager.");
            return true;
        } else {
            getLogger().at(Level.WARNING).log("Authentication failed.");
            return false;
        }
    }

    public boolean refreshTokens() {
        return performLogin(true);
    }

    // --- Планировщик периодического обновления ---
    private void startRefreshScheduler() {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        int interval = config.getRefreshIntervalHours();
        refreshTask = scheduler.scheduleAtFixedRate(() -> {
            getLogger().at(Level.INFO).log("Scheduled token refresh...");
            performLogin(true);
        }, interval, interval, TimeUnit.HOURS);
        getLogger().at(Level.INFO).log("Scheduled token refresh every " + interval + " hours.");
    }

    // --- Перезагрузка конфигурации (вызывается из команды) ---
    public void reloadConfig() {
        this.config = loadConfig();
        getLogger().at(Level.INFO).log("Configuration reloaded.");

        // Перезапускаем планировщик с новым интервалом
        if (refreshTask != null) {
            refreshTask.cancel(false);
            refreshTask = null;
        }
        if (config.getRefreshIntervalHours() > 0) {
            startRefreshScheduler();
        }
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("AuthFree shutting down...");
        if (refreshTask != null) {
            refreshTask.cancel(false);
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public AuthFreeConfig getConfig() { return config; }
    public AuthFreeData getTokenData() { return tokenData; }
}


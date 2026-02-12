package dev.hytalemodding.authfree.config;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class AuthFreeConfig {

    @SerializedName("auth_server")
    private String authServer = "https://sessions.sanasol.ws/server/auto-auth";

    @SerializedName("server_name")
    private String serverName = "Hytale Server";

    @SerializedName("server_id")
    private String serverId;

    @SerializedName("auth_on_startup")
    private boolean authOnStartup = true;

    @SerializedName("refresh_interval_hours")
    private int refreshIntervalHours = 12; // 0 = disabled

    public String getAuthServer() {
        return authServer;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerId() {
        if (serverId == null || serverId.isEmpty()) {
            generateServerId();
        }
        return serverId;
    }

    public boolean isAuthOnStartup() {
        return authOnStartup;
    }

    public int getRefreshIntervalHours() {
        return refreshIntervalHours;
    }

    public void setAuthServer(String authServer) {
        this.authServer = authServer;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setAuthOnStartup(boolean authOnStartup) {
        this.authOnStartup = authOnStartup;
    }

    public void setRefreshIntervalHours(int refreshIntervalHours) {
        this.refreshIntervalHours = refreshIntervalHours;
    }

    public void generateServerIdIfMissing() {
        if (serverId == null || serverId.isEmpty()) {
            serverId = UUID.randomUUID().toString();
        }
    }

    private void generateServerId() {
        if (serverId == null || serverId.isEmpty()) {
            serverId = UUID.randomUUID().toString();
        }
    }
}

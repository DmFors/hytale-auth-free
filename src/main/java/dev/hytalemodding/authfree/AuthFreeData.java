package dev.hytalemodding.authfree;

import com.google.gson.annotations.SerializedName;

public class AuthFreeData {

    @SerializedName("session_token")
    private String sessionToken;

    @SerializedName("identity_token")
    private String identityToken;

    @SerializedName("last_refresh")
    private long lastRefresh; // миллисекунды

    @SerializedName("expires_at")
    private long expiresAt;   // миллисекунды

    public String getSessionToken() { return sessionToken; }
    public String getIdentityToken() { return identityToken; }
    public long getLastRefresh() { return lastRefresh; }
    public long getExpiresAt() { return expiresAt; }

    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
    public void setIdentityToken(String identityToken) { this.identityToken = identityToken; }
    public void setLastRefresh(long lastRefresh) { this.lastRefresh = lastRefresh; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }

    public boolean hasValidToken() {
        return sessionToken != null && !sessionToken.isEmpty()
                && expiresAt > System.currentTimeMillis();
    }

    public boolean isTokenPresent() {
        return sessionToken != null && !sessionToken.isEmpty();
    }
}
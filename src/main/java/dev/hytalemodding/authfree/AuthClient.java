package dev.hytalemodding.authfree;

import com.hypixel.hytale.logger.HytaleLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;

public class AuthClient {

    private final HttpClient httpClient;
    private final HytaleLogger logger;

    public AuthClient(HytaleLogger logger) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.logger = logger;
    }

    public TokenResponse requestTokens(String authServerUrl, String serverId, String serverName) {
        try {
            String jsonPayload = String.format(
                    "{\"server_id\": \"%s\", \"server_name\": \"%s\"}",
                    serverId, serverName
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(authServerUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                String sessionToken = extractJsonValue(body, "sessionToken");
                String identityToken = extractJsonValue(body, "identityToken");
                if (sessionToken != null && identityToken != null) {
                    return new TokenResponse(sessionToken, identityToken);
                } else {
                    logger.at(Level.WARNING).log("Response missing tokens: " + body);
                }
            } else {
                logger.at(Level.WARNING).log("HTTP " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("Authentication request failed: " + e.getMessage());
        }
        return null;
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return (end == -1) ? null : json.substring(start, end);
    }

    public record TokenResponse(String sessionToken, String identityToken) {}
}
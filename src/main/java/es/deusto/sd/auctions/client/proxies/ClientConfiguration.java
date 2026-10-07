/**
 * Generated with assistance from ChatGPT-6.1 Sol (October 2026).
 */
package es.deusto.sd.auctions.client.proxies;

import java.io.IOException;
import java.util.Properties;

/** Loads the standalone clients' server URL from the same settings as the web client. */
public final class ClientConfiguration {
    private ClientConfiguration() { }

    public static String apiBaseUrl() {
        String override = System.getProperty("api.base.url", System.getenv("API_BASE_URL"));
        if (override != null && !override.isBlank()) return override;
        Properties properties = new Properties();
        try (var input = ClientConfiguration.class.getResourceAsStream("/application.properties")) {
            if (input != null) properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read application.properties", exception);
        }
        return properties.getProperty("api.base.url", "http://localhost:8082");
    }
}

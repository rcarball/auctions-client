/**
 * Entirely generated with ChatGPT-6.1 in September and October 2026.
 * Reviewed and updated with assistance from ChatGPT-6.1 Sol (October 2026).
 */
package es.deusto.sd.auctions.client.proxies;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.codec.digest.DigestUtils;
import es.deusto.sd.auctions.client.data.Credentials;

class ServiceProxyContractTest {
    private HttpServer server;
    private List<IAuctionsServiceProxy> proxies;
    private int status;
    private String response;
    private final AtomicReference<String> path = new AtomicReference<>();
    private final AtomicReference<String> body = new AtomicReference<>();
    private final AtomicReference<String> contentType = new AtomicReference<>();

    @BeforeEach void startServer() throws Exception {
        status = 204;
        response = "";
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            path.set(exchange.getRequestURI().getPath());
            body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
            exchange.getResponseHeaders().set("Content-Type", path.get().equals("/auth/login") ? "text/plain" : "application/json");
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, status == 204 ? -1 : bytes.length);
            try (var output = exchange.getResponseBody()) { if (status != 204) output.write(bytes); }
        });
        server.start();
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        RestTemplateServiceProxy rest = new RestTemplateServiceProxy(new RestTemplate());
        ReflectionTestUtils.setField(rest, "apiBaseUrl", baseUrl);
        proxies = List.of(new HttpServiceProxy(baseUrl), rest);
    }

    @AfterEach void stopServer() { server.stop(0); }

    @Test void encodesCategoryNamesAndHandlesEmptyLists() {
        for (var proxy : proxies) {
            assertTrue(proxy.getAllCategories().isEmpty());
            for (String category : List.of("Sporting Goods", "Books+100% & Stuff")) {
                assertTrue(proxy.getArticlesByCategory(category, "EUR").isEmpty());
                assertEquals("/auctions/categories/" + category + "/articles", path.get());
            }
        }
    }

    @Test void hashesPasswordsOnceAndReadsPlainTextTokens() {
        status = 200;
        response = "session-token";
        for (var proxy : proxies) {
            assertEquals("session-token", proxy.login(new Credentials("student", "password")));
            assertTrue(body.get().contains(DigestUtils.sha1Hex("password")));
            assertFalse(body.get().contains("\"password\":\"password\""));
        }
    }

    @Test void sendsPlainTextTokensForBidsAndLogout() {
        for (var proxy : proxies) {
            proxy.makeBid(1L, 101f, "EUR", "session-token");
            assertEquals("session-token", body.get());
            assertTrue(contentType.get().startsWith("text/plain"));
            proxy.logout("session-token");
            assertEquals("session-token", body.get());
        }
    }

    @Test void reportsRejectedBids() {
        for (var proxy : proxies) {
            for (int error : List.of(400, 401, 404, 409, 410)) {
                status = error;
                assertThrows(RuntimeException.class, () -> proxy.makeBid(1L, 101f, "EUR", "session-token"));
            }
        }
    }
}

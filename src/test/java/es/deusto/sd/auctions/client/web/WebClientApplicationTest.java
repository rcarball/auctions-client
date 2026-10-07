/**
 * Entirely generated with ChatGPT-6.1 in September and October 2026.
 * Reviewed and updated with assistance from ChatGPT-6.1 Sol (October 2026).
 */
package es.deusto.sd.auctions.client.web;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebClientApplicationTest {
    private static final HttpServer backend = startBackend();
    @Value("${local.server.port}") private int port;
    private final HttpClient client = HttpClient.newHttpClient();

    @DynamicPropertySource
    static void configuration(DynamicPropertyRegistry properties) {
        properties.add("api.base.url", () -> "http://127.0.0.1:" + backend.getAddress().getPort());
    }

    private static HttpServer startBackend() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();
                String body = "";
                int status = 200;
                if (path.equals("/auctions/categories")) body = "[{\"name\":\"Sporting Goods\"}]";
                else if (path.endsWith("/articles")) body = "[]";
                else if (path.equals("/auctions/articles/1/details")) body = "{\"id\":1,\"title\":\"Laptop\",\"initialPrice\":100.0,\"currentPrice\":125.0,\"bids\":1,\"auctionEnd\":\"2026-12-31T23:59:59Z\",\"categoryName\":\"Sporting Goods\",\"ownerName\":\"owner\",\"currency\":\"EUR\"}";
                else status = 404;
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, bytes.length);
                try (var output = exchange.getResponseBody()) { output.write(bytes); }
            });
            server.start();
            return server;
        } catch (Exception exception) { throw new IllegalStateException(exception); }
    }

    @AfterAll static void stopBackend() { backend.stop(0); }

    @Test void rendersHomeCategoryAndArticleTemplates() throws Exception {
        assertTrue(get("/").body().contains("Sporting Goods"));
        assertEquals(200, get("/category/Sporting%20Goods").statusCode());
        var article = get("/article/1");
        assertEquals(200, article.statusCode());
        assertTrue(article.body().contains("Laptop"));
    }

    @Test void rendersAnErrorWhenTheServerCannotFindTheArticle() throws Exception {
        var response = get("/article/999");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Article not found"));
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}

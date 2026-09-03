package es.deusto.sd.auctions.client.console;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.deusto.sd.auctions.client.data.Article;
import es.deusto.sd.auctions.client.data.Category;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;

@ExtendWith(MockitoExtension.class)
class ConsoleClientTest {

    @Mock
    private IAuctionsServiceProxy proxy;

    private ConsoleClient client;

    @BeforeEach
    void setUp() {
        client = new ConsoleClient(proxy);
    }

    @Test
    void workflowLogsInLoadsAnArticleAndPlacesAHigherBid() {
        Article article = new Article(1L, "Laptop", 100.0f, 100.0f, 0,
                new Date(), "Electronics", "owner", "EUR");
        when(proxy.login(any())).thenReturn("token-123");
        when(proxy.getAllCategories()).thenReturn(List.of(new Category("Electronics")));
        when(proxy.getArticlesByCategory("Electronics", "EUR")).thenReturn(List.of(article));
        when(proxy.getArticleDetails(1L, "EUR")).thenReturn(article);

        assertTrue(client.performLogin());
        assertTrue(client.loadArticlesAndPlaceBid());

        verify(proxy).makeBid(1L, 101.0f, "EUR", "token-123");
    }

    @Test
    void categoryLoadingFailsCleanlyWhenTheServerReturnsNoCategories() {
        when(proxy.getAllCategories()).thenReturn(List.of());

        assertFalse(client.loadCategories());
    }

    @Test
    void articleDetailsFailureDoesNotStopTheClientWithAnException() {
        when(proxy.getArticleDetails(99L, "EUR")).thenThrow(new RuntimeException("Article not found"));

        assertTrue(client.loadArticleDetails(99L) == null);
    }
}

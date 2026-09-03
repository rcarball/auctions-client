package es.deusto.sd.auctions.client.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.deusto.sd.auctions.client.data.Category;
import es.deusto.sd.auctions.client.data.Credentials;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;

@ExtendWith(MockitoExtension.class)
class SwingClientControllerTest {

    @Mock
    private IAuctionsServiceProxy proxy;

    private SwingClientController controller;

    @BeforeEach
    void setUp() {
        controller = new SwingClientController(proxy);
    }

    @Test
    void loginStoresTheTokenUsedWhenPlacingABid() {
        when(proxy.login(new Credentials("student@example.com", "password"))).thenReturn("token-123");

        assertEquals(true, controller.login("student@example.com", "password"));
        controller.placeBid(5L, 101.0f, "EUR");

        verify(proxy).makeBid(5L, 101.0f, "EUR", "token-123");
    }

    @Test
    void loginReportsTheProxyFailureWithContext() {
        when(proxy.login(new Credentials("student@example.com", "invalid")))
                .thenThrow(new RuntimeException("Unauthorized"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.login("student@example.com", "invalid"));

        assertEquals("Login failed: Unauthorized", exception.getMessage());
    }

    @Test
    void categoryQueriesAreDelegatedToTheProxy() {
        List<Category> categories = List.of(new Category("Books"));
        when(proxy.getAllCategories()).thenReturn(categories);

        assertEquals(categories, controller.getCategories());
    }
}

package es.deusto.sd.auctions.client.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.deusto.sd.auctions.client.data.Category;
import es.deusto.sd.auctions.client.data.Credentials;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;

@ExtendWith(MockitoExtension.class)
class WebClientControllerTest {

    private static final View NO_OP_VIEW = new View() {
        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
            // The assertions target the controller's model and view name, not template rendering.
        }
    };

    @Mock
    private IAuctionsServiceProxy proxy;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ViewResolver testViewResolver = (viewName, locale) -> viewName.startsWith("redirect:")
                ? new RedirectView(viewName.substring("redirect:".length()), true)
                : NO_OP_VIEW;
        mockMvc = MockMvcBuilders.standaloneSetup(new WebClientController(proxy))
                .setViewResolvers(testViewResolver)
                .build();
    }

    @Test
    void homeShowsCategoriesProvidedByTheServer() throws Exception {
        when(proxy.getAllCategories()).thenReturn(List.of(new Category("Electronics")));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void successfulLoginStoresTokenInTheUserSession() throws Exception {
        when(proxy.login(new Credentials("student@example.com", "password"))).thenReturn("token-123");

        mockMvc.perform(post("/login")
                .param("email", "student@example.com")
                .param("password", "password")
                .param("redirectUrl", "/category/Electronics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/category/Electronics"))
                .andExpect(request().sessionAttribute("token", "token-123"));
    }

    @Test
    void failedLoginReturnsTheLoginViewWithAnError() throws Exception {
        when(proxy.login(any(Credentials.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/login")
                .param("email", "student@example.com")
                .param("password", "invalid"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("errorMessage", "Login failed: Invalid credentials"));
    }

    @Test
    void bidUsesTheTokenFromItsOwnSessionAndShowsSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("token", "token-123");

        mockMvc.perform(post("/bid")
                .session(session)
                .param("id", "7")
                .param("amount", "125")
                .param("currency", "EUR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/article/7?currency=EUR"))
                .andExpect(flash().attribute("successMessage", "Bid placed successfully!"));

        verify(proxy).makeBid(7L, 125.0f, "EUR", "token-123");
    }

    @Test
    void failedBidShowsAnErrorAfterRedirectingBackToTheArticle() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("token", "token-123");
        doThrow(new RuntimeException("The auction has ended"))
                .when(proxy).makeBid(eq(7L), eq(125.0f), eq("EUR"), eq("token-123"));

        mockMvc.perform(post("/bid")
                .session(session)
                .param("id", "7")
                .param("amount", "125"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Failed to place bid: The auction has ended"));
    }
}

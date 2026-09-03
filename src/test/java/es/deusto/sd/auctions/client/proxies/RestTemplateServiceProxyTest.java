package es.deusto.sd.auctions.client.proxies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import es.deusto.sd.auctions.client.data.Credentials;

@ExtendWith(MockitoExtension.class)
class RestTemplateServiceProxyTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpStatusCodeException httpException;

    private RestTemplateServiceProxy proxy;

    @BeforeEach
    void setUp() {
        proxy = new RestTemplateServiceProxy(restTemplate);
        ReflectionTestUtils.setField(proxy, "apiBaseUrl", "http://auctions.example");
    }

    @Test
    void loginHashesThePasswordBeforeSendingCredentials() {
        when(restTemplate.postForObject(eq("http://auctions.example/auth/login"), any(), eq(String.class)))
                .thenReturn("token-123");

        assertEquals("token-123", proxy.login(new Credentials("student@example.com", "password")));

        ArgumentCaptor<Credentials> credentials = ArgumentCaptor.forClass(Credentials.class);
        verify(restTemplate).postForObject(eq("http://auctions.example/auth/login"), credentials.capture(), eq(String.class));
        assertEquals("student@example.com", credentials.getValue().email());
        assertEquals(DigestUtils.sha1Hex("password"), credentials.getValue().password());
    }

    @Test
    void bidMapsAConflictResponseToTheDomainMessage() {
        when(httpException.getStatusCode()).thenReturn(HttpStatus.CONFLICT);
        when(restTemplate.postForObject(eq("http://auctions.example/auctions/articles/7/bid?amount=100.0&currency=EUR"),
                eq("token-123"), eq(Void.class))).thenThrow(httpException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> proxy.makeBid(7L, 100.0f, "EUR", "token-123"));

        assertEquals("Bid amount must be greater than the current price", exception.getMessage());
    }
}

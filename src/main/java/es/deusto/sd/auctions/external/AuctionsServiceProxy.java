package es.deusto.sd.auctions.external;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import es.deusto.sd.auctions.dto.Article;
import es.deusto.sd.auctions.dto.Category;
import es.deusto.sd.auctions.dto.Credentials;

@Service
public class AuctionsServiceProxy {

    private final RestTemplate restTemplate;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public AuctionsServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String login(Credentials credentials) {
        String url = apiBaseUrl + "/auth/login";
        try {
            HttpEntity<Credentials> request = new HttpEntity<>(credentials);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Login failed: Invalid credentials.");
            } else {
                throw new RuntimeException("Login failed: " + e.getStatusText());
            }
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Login failed: Server error. Please try again later.");
        }
    }

    public void logout(String token) {
        String url = apiBaseUrl + "/auth/logout";
        try {
            HttpEntity<String> request = new HttpEntity<>(token);
            restTemplate.postForEntity(url, request, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Logout failed: Invalid token.");
            } else {
                throw new RuntimeException("Logout failed: " + e.getStatusText());
            }
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Logout failed: Server error. Please try again later.");
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Category> getAllCategories() {
        String url = apiBaseUrl + "/auctions/categories";
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NO_CONTENT) {
                throw new RuntimeException("No categories found.");
            } else {
                throw new RuntimeException("Failed to retrieve categories: " + e.getStatusText());
            }
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Failed to retrieve categories: Server error.");
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Article> getArticlesByCategory(String categoryName, String currency) {
        String url = apiBaseUrl + "/auctions/categories/" + categoryName + "/articles?currency=" + currency;
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Category not found: " + categoryName);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Invalid currency: " + currency);
            } else {
                throw new RuntimeException("Failed to retrieve articles: " + e.getStatusText());
            }
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Failed to retrieve articles: Server error.");
        }
    }

    public Article getArticleDetails(Long articleId, String currency) {
        String url = apiBaseUrl + "/auctions/articles/" + articleId + "/details?currency=" + currency;
        try {
            ResponseEntity<Article> response = restTemplate.getForEntity(url, Article.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Article not found: ID " + articleId);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Invalid currency: " + currency);
            } else {
                throw new RuntimeException("Failed to retrieve article details: " + e.getStatusText());
            }
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Failed to retrieve article details: Server error.");
        }
    }

    public void makeBid(Long articleId, double amount, String currency, String token) {
        String url = apiBaseUrl + "/auctions/articles/" + articleId + "/bid?amount=" + amount + "&currency=" + currency;
        try {
            HttpEntity<String> request = new HttpEntity<>(token);
            restTemplate.postForEntity(url, request, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Article not found: ID " + articleId);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Currency not supported: " + currency);
            } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new RuntimeException("Bid amount must be greater than the current price.");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("User not authenticated.");
            } else {
                throw new RuntimeException("Failed to place bid: " + e.getStatusText());
            }
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Failed to place bid: Server error.");
        }
    }
}
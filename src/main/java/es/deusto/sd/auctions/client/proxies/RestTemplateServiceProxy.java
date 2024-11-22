/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.proxies;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import es.deusto.sd.auctions.client.data.Article;
import es.deusto.sd.auctions.client.data.Category;
import es.deusto.sd.auctions.client.data.Credentials;

@Service
public class RestTemplateServiceProxy {

    private final RestTemplate restTemplate;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public RestTemplateServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String login(Credentials credentials) {
        String url = apiBaseUrl + "/auth/login";
        
        try {
            return restTemplate.postForObject(url, credentials, String.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RuntimeException("Login failed: Invalid credentials.");
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Login failed: " + e.getStatusText());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Login failed: Server error. Please try again later.");
        }
    }

    public void logout(String token) {
        String url = apiBaseUrl + "/auth/logout";
        
        try {
            restTemplate.postForObject(url, token, Void.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RuntimeException("Logout failed: Invalid token.");
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Logout failed: " + e.getStatusText());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Logout failed: Server error. Please try again later.");
        }
    }

    @SuppressWarnings("unchecked")
    public List<Category> getAllCategories() {
        String url = apiBaseUrl + "/auctions/categories";
        
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("No categories found.");
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to retrieve categories: " + e.getStatusText());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Failed to retrieve categories: Server error.");
        }
    }

    @SuppressWarnings("unchecked")
    public List<Article> getArticlesByCategory(String categoryName, String currency) {
        String url = String.format("%s/auctions/categories/%s/articles?currency=%s", apiBaseUrl, categoryName, currency);
        
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Category not found: " + categoryName);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new RuntimeException("Invalid currency: " + currency);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to retrieve articles: " + e.getStatusText());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Failed to retrieve articles: Server error.");
        }
    }

    public Article getArticleDetails(Long articleId, String currency) {
        String url = String.format("%s/auctions/articles/%d/details?currency=%s", apiBaseUrl, articleId, currency);
        
        try {
            return restTemplate.getForObject(url, Article.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Article not found: ID " + articleId);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new RuntimeException("Invalid currency: " + currency);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to retrieve article details: " + e.getStatusText());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Failed to retrieve article details: Server error.");
        }
    }

    public void makeBid(Long articleId, float amount, String currency, String token) {
        String url = String.format("%s/auctions/articles/%d/bid?amount=%f&currency=%s", apiBaseUrl, articleId, amount, currency);
        
        try {
            restTemplate.postForObject(url, token, Void.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 401 -> throw new RuntimeException("User not authenticated");
                case 404 -> throw new RuntimeException("Article not found");
                case 400 -> throw new RuntimeException("Invalid currency: " + currency);
                case 409 -> throw new RuntimeException("Bid amount must be greater than the current price");
                case 204 -> { /* Successful bid */ }
                case 500 -> throw new RuntimeException("Internal server error while processing bid");
                default -> throw new RuntimeException("Bid failed with status code: " + e.getStatusCode());
            }
        }
    }
}
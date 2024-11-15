package es.deusto.sd.auctions.swing;

import es.deusto.sd.auctions.BasicServiceProxy;
import es.deusto.sd.auctions.dto.Article;
import es.deusto.sd.auctions.dto.Category;
import es.deusto.sd.auctions.dto.Credentials;

import java.util.List;

public class SwingClientController {   
	private BasicServiceProxy apiClient;
    private String token;

    public SwingClientController() {
        this.apiClient = new BasicServiceProxy();
    }

    public boolean login(String email, String password) {
        try {
            Credentials credentials = new Credentials(email, password);
            token = apiClient.login(credentials);
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public void logout() {
        apiClient.logout(token);
    }

    public List<Category> getCategories() {
        return apiClient.getAllCategories();
    }

    public List<Article> getArticlesByCategory(String categoryName, String currency) {
        return apiClient.getArticlesByCategory(categoryName, currency);
    }

    public Article getArticleDetails(Long articleId, String currency) {
        return apiClient.getArticleDetails(articleId, currency);
    }

    public void placeBid(Long articleId, Float amount, String currency) {
        apiClient.makeBid(articleId, amount, currency, token);
    }
}
package es.deusto.sd.auctions.swing;

import es.deusto.sd.auctions.ApiClient;
import es.deusto.sd.auctions.dto.ArticleDTO;
import es.deusto.sd.auctions.dto.CategoryDTO;
import es.deusto.sd.auctions.dto.CredentialsDTO;

import java.util.List;

public class SwingClientController {   
	private ApiClient apiClient;
    private String token;

    public SwingClientController() {
        this.apiClient = new ApiClient();
    }

    public boolean login(String email, String password) {
        try {
            CredentialsDTO credentials = new CredentialsDTO(email, password);
            token = apiClient.login(credentials);
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public void logout() {
        apiClient.logout(token);
    }

    public List<CategoryDTO> getCategories() {
        return apiClient.getAllCategories();
    }

    public List<ArticleDTO> getArticlesByCategory(String categoryName, String currency) {
        return apiClient.getArticlesByCategory(categoryName, currency);
    }

    public ArticleDTO getArticleDetails(Long articleId, String currency) {
        return apiClient.getArticleDetails(articleId, currency);
    }

    public void placeBid(Long articleId, Float amount, String currency) {
        apiClient.makeBid(articleId, amount, currency, token);
    }
}
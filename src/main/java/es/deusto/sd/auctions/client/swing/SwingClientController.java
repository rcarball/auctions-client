/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.swing;

import es.deusto.sd.auctions.client.Article;
import es.deusto.sd.auctions.client.Category;
import es.deusto.sd.auctions.client.Credentials;
import es.deusto.sd.auctions.client.external.HttpServiceProxy;

import java.util.List;

/**
 * SwingClientController class is a Controller class that manages the communication
 * between the SwingClient (the view) and the HttpServiceProxy.
 */
public class SwingClientController {   
	private HttpServiceProxy serviceProxy;
    private String token;

    public SwingClientController() {
        this.serviceProxy = new HttpServiceProxy();
    }
        
	public boolean login(String email, String password) {
        try {
            Credentials credentials = new Credentials(email, password);
            token = serviceProxy.login(credentials);
            
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public void logout() {
        serviceProxy.logout(token);
    }

    public List<Category> getCategories() {
        return serviceProxy.getAllCategories();
    }

    public List<Article> getArticlesByCategory(String categoryName, String currency) {
        return serviceProxy.getArticlesByCategory(categoryName, currency);
    }

    public Article getArticleDetails(Long articleId, String currency) {
        return serviceProxy.getArticleDetails(articleId, currency);
    }

    public void placeBid(Long articleId, Float amount, String currency) {
        serviceProxy.makeBid(articleId, amount, currency, token);
    }
}
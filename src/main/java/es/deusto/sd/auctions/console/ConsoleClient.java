package es.deusto.sd.auctions.console;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.deusto.sd.auctions.dto.ArticleDTO;
import es.deusto.sd.auctions.dto.CategoryDTO;
import es.deusto.sd.auctions.dto.CredentialsDTO;
import es.deusto.sd.auctions.external.AuctionsServiceProxy;

@Component
public class ConsoleClient {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleClient.class);
    
    private AuctionsServiceProxy auctionsServiceProxy;
    
    private String token;
    private String defaultEmail = "blackwidow@marvel.com";
    private String defaultPassword = "Bl@ckWid0w2023";
    
	public ConsoleClient(AuctionsServiceProxy auctionsServiceProxy) {
        this.auctionsServiceProxy = auctionsServiceProxy;
	}
	
    public boolean performLogin() {
        CredentialsDTO credentials = new CredentialsDTO(defaultEmail, defaultPassword);
        try {
            token = auctionsServiceProxy.login(credentials);
            logger.info("Login successful, token received.");
            return true;
        } catch (RuntimeException e) {
            logger.error("Login failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean loadCategories() {
        try {
            List<CategoryDTO> categories = auctionsServiceProxy.getAllCategories();
            if (categories == null || categories.isEmpty()) {
                logger.info("No categories found.");
                return false;
            }
            categories.forEach(category -> logger.info("Category: {}", category.name()));
            return true;
        } catch (RuntimeException e) {
            logger.error("Failed to load categories: {}", e.getMessage());
            return false;
        }
    }

    public boolean loadArticlesAndPlaceBid() {
        String categoryName = "Electronics";
        String currency = "EUR";
        Long articleId = 1L; // ID del artículo en el que se hará la puja
        double bidAmount = 150.00;

        try {
            List<ArticleDTO> articles = auctionsServiceProxy.getArticlesByCategory(categoryName, currency);
            if (articles == null || articles.isEmpty()) {
                logger.info("No articles found in category: {}", categoryName);
                return false;
            }
            articles.forEach(article -> logger.info("Article: {} - Current Price: {}", article.title(), article.currentPrice()));

            // Hacer una puja en el primer artículo
            auctionsServiceProxy.makeBid(articleId, bidAmount, currency, token);
            logger.info("Bid placed successfully on article ID {}", articleId);
            return true;
        } catch (RuntimeException e) {
            logger.error("Failed to load articles or place bid: {}", e.getMessage());
            return false;
        }
    }
}
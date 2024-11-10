package es.deusto.sd.auctions.console;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.deusto.sd.auctions.ClientController;
import es.deusto.sd.auctions.dto.ArticleDTO;
import es.deusto.sd.auctions.dto.CategoryDTO;
import es.deusto.sd.auctions.dto.CredentialsDTO;

public class ConsoleClient {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleClient.class);
	private ClientController clientController = new ClientController();
	private String token;

	private String defaultEmail = "blackwidow@marvel.com";
	private String defaultPassword = "Bl@ckWid0w2023";

	public static void main(String[] args) {
		ConsoleClient client = new ConsoleClient();
		if (!client.performLogin() || !client.loadCategories() || !client.loadArticlesAndPlaceBid()) {
			logger.info("Exiting application due to failure in one of the steps.");
		}
	}

	private boolean performLogin() {
		CredentialsDTO credentials = new CredentialsDTO(defaultEmail, defaultPassword);
		try {
			token = clientController.login(credentials);
			logger.info("Login successful. Token: {}", token);
			return true;
		} catch (RuntimeException e) {
			logger.error("Login failed: {}", e.getMessage());
			return false;
		}
	}

	private boolean loadCategories() {
		try {
			List<CategoryDTO> categories = clientController.getAllCategories();

			if (categories.isEmpty()) {
				logger.info("No categories found.");
				return false;
			}

			logger.info("Categories retrieved:");
			categories.forEach(category -> logger.info("- {}", category.name()));
			return true;
		} catch (RuntimeException e) {
			logger.error("Failed to load categories: {}", e.getMessage());
			return false;
		}
	}

	private boolean loadArticlesAndPlaceBid() {
		List<CategoryDTO> categories = clientController.getAllCategories();
		String categoryName = categories.get(0).name();
		logger.info("Fetching articles for category: {}", categoryName);

		List<ArticleDTO> articles;

		try {
			articles = clientController.getArticlesByCategory(categoryName, "EUR");

			if (articles.isEmpty()) {
				logger.info("No articles found in category: {}", categoryName);
				return false;
			}

			ArticleDTO articleDetails = loadArticleDetails(articles.get(0).id());
			return articleDetails != null && placeBid(articleDetails);
		} catch (RuntimeException e) {
			logger.error("Failed to fetch articles by category: {}", e.getMessage());
			return false;
		}
	}

	private ArticleDTO loadArticleDetails(Long articleId) {
		try {
			ArticleDTO article = clientController.getArticleDetails(articleId, "EUR");
			logger.info("Article Details - Title: {}, Current Price: {} {}, Bids: {}", article.title(),
					article.currentPrice(), article.currency(), article.bids());
			return article;
		} catch (RuntimeException e) {
			logger.error("Failed to load article details: {}", e.getMessage());
			return null;
		}
	}

	private boolean placeBid(ArticleDTO article) {
		Float bidAmount = article.currentPrice() + 1.0f;
		try {
			clientController.makeBid(article.id(), bidAmount, article.currency(), token);
			logger.info("Bid placed successfully on article ID {} with amount {} {}", article.id(), bidAmount,
					article.currency());
			return true;
		} catch (RuntimeException e) {
			logger.error("Failed to place bid: {}", e.getMessage());
			return false;
		}
	}
}
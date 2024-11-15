package es.deusto.sd.auctions.console;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.deusto.sd.auctions.dto.Article;
import es.deusto.sd.auctions.dto.Category;
import es.deusto.sd.auctions.dto.Credentials;
import es.deusto.sd.auctions.external.HttpServiceProxy;

public class ConsoleClient {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleClient.class);
	private final HttpServiceProxy auctionsServiceProxy = new HttpServiceProxy();
	private String token;

	private String defaultEmail = "blackwidow@marvel.com";
	private String defaultPassword = "Bl@ckWid0w2023";

	public static void main(String[] args) {
		ConsoleClient client = new ConsoleClient();
		if (!client.performLogin() || !client.loadCategories() || !client.loadArticlesAndPlaceBid()) {
			logger.info("Exiting application due to failure in one of the steps.");
		}
	}

	public boolean performLogin() {
		Credentials credentials = new Credentials(defaultEmail, defaultPassword);
		try {
			token = auctionsServiceProxy.login(credentials);
			logger.info("Login successful. Token: {}", token);
			return true;
		} catch (RuntimeException e) {
			logger.error("Login failed: {}", e.getMessage());
			return false;
		}
	}

	public boolean loadCategories() {
		try {
			List<Category> categories = auctionsServiceProxy.getAllCategories();
			if (categories == null || categories.isEmpty()) {
				logger.info("No categories found.");
				return false;
			}
			categories.forEach(category -> logger.info("Category: {}", category.name()));
			return true;
		} catch (RuntimeException e) {
			logger.error("Failed to load categories: {}", e.getMessage());
		}
		return false;
	}

	public boolean loadArticlesAndPlaceBid() {
		List<Category> categories = auctionsServiceProxy.getAllCategories();
		String categoryName = categories.get(0).name();
		logger.info("Fetching articles for category: {}", categoryName);

		List<Article> articles;

		try {
			articles = auctionsServiceProxy.getArticlesByCategory(categoryName, "EUR");

			if (articles.isEmpty()) {
				logger.info("No articles found in category: {}", categoryName);
				return false;
			}

			Article articleDetails = loadArticleDetails(articles.get(0).id());
			return articleDetails != null && placeBid(articleDetails);
		} catch (RuntimeException e) {
			logger.error("Failed to fetch articles by category: {}", e.getMessage());
			return false;
		}
	}

	public Article loadArticleDetails(Long articleId) {
		try {
			Article article = auctionsServiceProxy.getArticleDetails(articleId, "EUR");
			logger.info("Article Details - Title: {}, Current Price: {} {}, Bids: {}", article.title(),
					article.currentPrice(), article.currency(), article.bids());
			return article;
		} catch (RuntimeException e) {
			logger.error("Failed to load article details: {}", e.getMessage());
			return null;
		}
	}

	public boolean placeBid(Article article) {
		Float bidAmount = article.currentPrice() + 1.0f;
		try {
			auctionsServiceProxy.makeBid(article.id(), bidAmount, article.currency(), token);
			logger.info("Bid placed successfully on article ID {} with amount {} {}", article.id(), bidAmount,
					article.currency());
			return true;
		} catch (RuntimeException e) {
			logger.error("Failed to place bid: {}", e.getMessage());
			return false;
		}
	}
}
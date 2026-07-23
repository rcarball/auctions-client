/**
 * This code was originally generated with Claude Sonnet 3.5 and adapted using GitHub
 * Copilot. It was reviewed, corrected and updated in July 2026 with the
 * assistance of Claude Opus 4.8 (Anthropic).
 */
package es.deusto.sd.auctions.client.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.deusto.sd.auctions.client.data.Article;
import es.deusto.sd.auctions.client.data.Category;
import es.deusto.sd.auctions.client.data.Credentials;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * WebClientController class serves as the primary controller for the web client
 * application built with Spring Boot. It orchestrates the interactions between
 * the web application and the AuctionsService through the
 * RestTemplateServiceProxy, managing HTTP requests and responses while serving
 * Thymeleaf templates.
 * 
 * The use of the `@Controller` annotation in the WebClientController class
 * signifies that this class serves as a front controller in the Spring MVC
 * architecture. This annotation allows Spring to recognize and manage the class
 * as a web component, enabling it to handle HTTP requests and produce responses
 * based on user interactions.
 * 
 * Spring Boot's `@Controller` facilitates the use of model attributes through
 * the `Model` interface. The `model.addAttribute()` method is used to add
 * attributes to the model, making them accessible in the Thymeleaf templates.
 * This method takes a key-value pair, where the key is the name of the
 * attribute that can be referenced in the template, and the value is the actual
 * data to be passed. For instance, when `model.addAttribute("currentUrl",
 * currentUrl)` is called, the current URL is stored in the model with the key
 * "currentUrl", allowing it to be easily accessed in the corresponding
 * Thymeleaf view. This mechanism enables the dynamic rendering of content based
 * on the application state, ensuring that user interfaces are responsive and
 * adaptable to user interactions.
 * 
 * The methods of the controller return a `String`, which represents the name of
 * the Thymeleaf template to be rendered. This design pattern allows the
 * controller to define the appropriate view for each action. For instance, when
 * the `home` method is called, it returns the string "index", which tells
 * Spring to render the `index.html` Thymeleaf template. The mapping methods not
 * only process data but also dictate the presentation layer, facilitating a
 * clear separation between business logic and user interface concerns.
 * 
 * This class uses two distinct mappings to handle the login process, allowing
 * for a clear separation of responsibilities and improving code organization.
 * 
 * The `@GetMapping("/login")` method is responsible for displaying the login
 * page. This method prepares and returns the view containing the login form,
 * ensuring that users can easily access the interface needed to enter their
 * credentials.
 * 
 * On the other hand, the `@PostMapping("/login")` method handles the submission
 * of the form, processing user input, validating credentials, and managing the
 * authentication logic. This separation allows each method to have a single
 * responsibility, making the code easier to understand and maintain.
 * 
 * (Description generated with ChatGPT 4o mini)
 */
@Controller
public class WebClientController {

	// Session attribute key under which the per-user token is stored.
	private static final String TOKEN_ATTRIBUTE = "token";

	@Autowired
	private IAuctionsServiceProxy auctionsServiceProxy;

	// Add current URL and token to all views.
	// The token is read from the HttpSession so that each user has its OWN session
	// state. Storing it in a controller field would share it across ALL users, since
	// a @Controller is a singleton.
	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request, HttpSession session) {
		String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
		model.addAttribute("currentUrl", currentUrl); // Makes current URL available in all templates
		model.addAttribute("token", session.getAttribute(TOKEN_ATTRIBUTE)); // Per-session token
	}

	@GetMapping("/")
	public String home(Model model) {
		List<Category> categories;

		try {
			categories = auctionsServiceProxy.getAllCategories();
			model.addAttribute("categories", categories);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Failed to load categories: " + e.getMessage());
		}

		return "index";
	}

	@GetMapping("/login")
	public String showLoginPage(@RequestParam(value = "redirectUrl", required = false) String redirection,
								Model model) {
		// Add redirectUrl to the model if needed
		model.addAttribute("redirectUrl", redirection);

		return "login"; // Return your login template
	}

	@PostMapping("/login")
	public String performLogin(@RequestParam("email") String userEmail,
							   @RequestParam("password") String userPassword,
							   @RequestParam(value = "redirectUrl", required = false) String redirection,
							   HttpSession session,
							   Model model) {
		Credentials credentials = new Credentials(userEmail, userPassword);

		try {
			String token = auctionsServiceProxy.login(credentials);
			session.setAttribute(TOKEN_ATTRIBUTE, token); // Store the token in the user's session

			// Redirect to the original page or root if redirectUrl is null
			return "redirect:" + (redirection != null && !redirection.isEmpty() ? redirection : "/");
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
			return "login"; // Return to login page with error message
		}
	}

	@GetMapping("/logout")
	public String performLogout(@RequestParam(value = "redirectUrl", defaultValue = "/") String redirection,
								HttpSession session,
								Model model) {
		try {
			auctionsServiceProxy.logout((String) session.getAttribute(TOKEN_ATTRIBUTE));
			session.removeAttribute(TOKEN_ATTRIBUTE); // Clear the token from the session after logout
			model.addAttribute("successMessage", "Logout successful.");
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Logout failed: " + e.getMessage());
		}

		// Redirect to the specified URL after logout
		return "redirect:" + redirection;
	}

	@GetMapping("/category/{name}")
	public String getCategoryArticles(@PathVariable("name") String categoryName,
									  @RequestParam(value = "currency", defaultValue = "EUR") String selectedCurrency, 
									  Model model) {
		List<Article> articles;

		try {
			articles = auctionsServiceProxy.getArticlesByCategory(categoryName, selectedCurrency);
			model.addAttribute("articles", articles);
			model.addAttribute("categoryName", categoryName);
			model.addAttribute("selectedCurrency", selectedCurrency);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Failed to load articles for category: " + e.getMessage());
			model.addAttribute("articles", null);
			model.addAttribute("categoryName", categoryName);
			model.addAttribute("selectedCurrency", "EUR");
		}

		return "category";
	}

	@GetMapping("/article/{id}")
	public String getArticleDetails(@PathVariable("id") Long productId,
									@RequestParam(value = "currency", defaultValue = "EUR") String selectedCurrency,
									Model model) {
		Article article;

		try {
			article = auctionsServiceProxy.getArticleDetails(productId, selectedCurrency);
			model.addAttribute("article", article);
			model.addAttribute("selectedCurrency", selectedCurrency);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Failed to load article details: " + e.getMessage());
			model.addAttribute("article", null);
			model.addAttribute("selectedCurrency", "EUR");
		}

		return "article";
	}

	@PostMapping("/bid")
	public String makeBid(@RequestParam("id") Long productId,
						  @RequestParam("amount") Float bidAmount,
						  @RequestParam(value = "currency", defaultValue = "EUR") String selectedCurrency,
						  HttpSession session,
						  Model model,
						  RedirectAttributes redirectAttributes) {
		try {
			auctionsServiceProxy.makeBid(productId, bidAmount, selectedCurrency,
					(String) session.getAttribute(TOKEN_ATTRIBUTE));
			// RedirectAttributes are used to pass attributes to the redirected page
			// Add a success message to be displayed in the article view
			redirectAttributes.addFlashAttribute("successMessage", "Bid placed successfully!");
		} catch (RuntimeException e) {
			// Add an error message to be displayed in the article view
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to place bid: " + e.getMessage());
		}

		return "redirect:/article/" + productId + "?currency=" + selectedCurrency;
	}
}
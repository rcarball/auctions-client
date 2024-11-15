package es.deusto.sd.auctions.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.deusto.sd.auctions.BasicServiceProxy;
import es.deusto.sd.auctions.dto.Article;
import es.deusto.sd.auctions.dto.Category;
import es.deusto.sd.auctions.dto.Credentials;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class WebClientController {

    private final BasicServiceProxy apiClient = new BasicServiceProxy();
    private String token; // Stores the session token
    
    // Add token to all views
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("token", token); // Makes token available in all templates
    }
    
    // Helper method to add current URL to the model
    private void addCurrentUrl(Model model, HttpServletRequest request) {
        String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        model.addAttribute("currentUrl", currentUrl);
    }
    
    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        List<Category> categories;
        
        try {
            categories = apiClient.getAllCategories();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load categories: " + e.getMessage());
            return "index";
        }
        
        addCurrentUrl(model, request); // Add current URL to model
        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/category/{name}")
    public String getCategoryArticles(@PathVariable("name") String name, 
                                      @RequestParam(value = "currency", defaultValue = "EUR") String currency, 
                                      Model model, HttpServletRequest request) {
        List<Article> articles;
        
        try {
            articles = apiClient.getArticlesByCategory(name, currency);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load articles for category: " + e.getMessage());
            return "category";
        }
        
        addCurrentUrl(model, request); // Add current URL to model
        model.addAttribute("articles", articles);
        model.addAttribute("categoryName", name);
        model.addAttribute("selectedCurrency", currency);
        return "category";
    }

    @GetMapping("/article/{id}")
    public String getArticleDetails(@PathVariable("id") Long id, 
                                    @RequestParam(value = "currency", defaultValue = "EUR") String currency, 
                                    Model model, HttpServletRequest request) {       
        Article article;
        
        try {
            article = apiClient.getArticleDetails(id, currency);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load article details: " + e.getMessage());
            return "article";
        }
        
        addCurrentUrl(model, request); // Add current URL to model
        model.addAttribute("article", article);
        model.addAttribute("categoryName", article.categoryName());
        model.addAttribute("selectedCurrency", currency);
        return "article";
    }

    @PostMapping("/bid")
    public String placeBid(@RequestParam("articleId") Long articleId, 
                           @RequestParam("amount") Float amount, 
                           @RequestParam(value = "currency", defaultValue = "EUR") String currency, 
                           Model model, HttpServletRequest request) {
        try {
            apiClient.makeBid(articleId, amount, currency, token);
            
            addCurrentUrl(model, request); // Add current URL to model
            model.addAttribute("successMessage", "Bid placed successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to place bid: " + e.getMessage());
        }
        
        return "redirect:/article/" + articleId + "?currency=" + currency;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpServletRequest request) {
        addCurrentUrl(model, request); // Add current URL to model
        return "login";
    }
    
    @PostMapping("/login")
    public String performLogin(@RequestParam("email") String email, 
                               @RequestParam("password") String password, 
                               Model model) {
        Credentials credentials = new Credentials(email, password);
        try {
            token = apiClient.login(credentials);
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String performLogout(@RequestParam(value = "redirectUrl", defaultValue = "/") String redirectUrl, Model model) {
        try {
            apiClient.logout(token);
            token = null; // Clear the token after logout
            model.addAttribute("successMessage", "Logout successful.");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Logout failed: " + e.getMessage());
        }
        
        return "redirect:" + redirectUrl; // Redirect to the specified URL after logout
    }
}
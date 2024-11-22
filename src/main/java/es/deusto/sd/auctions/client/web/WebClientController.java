/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.deusto.sd.auctions.client.data.Article;
import es.deusto.sd.auctions.client.data.Category;
import es.deusto.sd.auctions.client.data.Credentials;
import es.deusto.sd.auctions.client.proxies.RestTemplateServiceProxy;
import jakarta.servlet.http.HttpServletRequest;

/**
 * WebClientController class is the Spring Boot controller for the web client application.
 */
@Controller
public class WebClientController {

    @Autowired
    private RestTemplateServiceProxy auctionsServiceProxy;
    
    private String token; // Stores the session token
    
    // Add current URL and token to all views
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        model.addAttribute("currentUrl", currentUrl); // Makes current URL available in all templates
        model.addAttribute("token", token); // Makes token available in all templates
    }
    
    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories;
        
        try {
            categories = auctionsServiceProxy.getAllCategories();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load categories: " + e.getMessage());
            return "index";
        }
        
        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/category/{name}")
    public String getCategoryArticles(@PathVariable("name") String name, 
                                      @RequestParam(value = "currency", defaultValue = "EUR") String currency, 
                                      Model model) {
        List<Article> articles;
        
        try {
            articles = auctionsServiceProxy.getArticlesByCategory(name, currency);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load articles for category: " + e.getMessage());
            return "category";
        }
        
        model.addAttribute("articles", articles);
        model.addAttribute("categoryName", name);
        model.addAttribute("selectedCurrency", currency);
        return "category";
    }

    @GetMapping("/article/{id}")
    public String getArticleDetails(@PathVariable("id") Long id, 
                                    @RequestParam(value = "currency", defaultValue = "EUR") String currency, 
                                    Model model) {       
        Article article;
        
        try {
            article = auctionsServiceProxy.getArticleDetails(id, currency);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load article details: " + e.getMessage());
            return "article";
        }
        
        model.addAttribute("article", article);
        model.addAttribute("categoryName", article.categoryName());
        model.addAttribute("selectedCurrency", currency);
        return "article";
    }

    @PostMapping("/bid")
    public String placeBid(@RequestParam("id") Long id, 
                           @RequestParam("amount") Float amount, 
                           @RequestParam(value = "currency", defaultValue = "EUR") String currency, 
                           Model model) {
        try {
            auctionsServiceProxy.makeBid(id, amount, currency, token);
            model.addAttribute("successMessage", "Bid placed successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to place bid: " + e.getMessage());
        }
        
        return "redirect:/article/" + id + "?currency=" + currency;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }
    
    @PostMapping("/login")
    public String performLogin(@RequestParam("email") String email, 
                               @RequestParam("password") String password, 
                               Model model) {
        Credentials credentials = new Credentials(email, password);
        try {
            token = auctionsServiceProxy.login(credentials);
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String performLogout(@RequestParam(value = "redirectUrl", defaultValue = "/") String redirectUrl, Model model) {
        try {
            auctionsServiceProxy.logout(token);
            token = null; // Clear the token after logout
            model.addAttribute("successMessage", "Logout successful.");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Logout failed: " + e.getMessage());
        }
        
        return "redirect:" + redirectUrl; // Redirect to the specified URL after logout
    }
}
/**
 * Initially generated with Claude Sonnet 3.5 and adapted using GitHub Copilot.
 * Reviewed and updated with assistance from Claude Opus 4.8 (July 2026)
 * and ChatGPT-6.1 Sol (October 2026).
 */
package es.deusto.sd.auctions.client.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * WebClientApplication class is the main class for the web client application.
 */

@SpringBootApplication(scanBasePackages = {"es.deusto.sd.auctions"})
public class WebClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebClientApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}

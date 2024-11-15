package es.deusto.sd.auctions.console;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ConsoleClientApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(ConsoleClientApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception : " + e.getMessage());
		}

	}

	public CommandLineRunner run(ApplicationContext context) {
		return args -> {
			ConsoleClient consoleClient = context.getBean(ConsoleClient.class);
			if (!consoleClient.performLogin() || !consoleClient.loadCategories()
					|| !consoleClient.loadArticlesAndPlaceBid()) {
				System.out.println("Exiting application due to failure in one of the steps.");
			}
		};
	}
}
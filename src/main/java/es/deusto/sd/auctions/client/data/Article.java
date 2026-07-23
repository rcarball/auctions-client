/**
 * This code was originally generated with Claude Sonnet 3.5 and adapted using GitHub
 * Copilot. It was reviewed, corrected and updated in July 2026 with the
 * assistance of Claude Opus 4.8 (Anthropic).
 */
package es.deusto.sd.auctions.client.data;

import java.util.Date;

public record Article(
	    Long id,
	    String title,
	    Float initialPrice,
	    Float currentPrice,
	    Integer bids,
	    Date auctionEnd,
	    String categoryName,
	    String ownerName,
	    String currency
	) {}
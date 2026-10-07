/**
 * Initially generated with Claude Sonnet 3.5 and adapted using GitHub Copilot.
 * Reviewed and updated with assistance from Claude Opus 4.8 (July 2026)
 * and ChatGPT-6.1 Sol (October 2026).
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
package com.github.ptrteixeira.nusports.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Factory for creating methods of attaching to web sites. Structured the way that it is because,
 * so far as I am concerned, each web scraper needs to be tailored for the site that it gets
 * attached to (it may be possible to do otherwise with NLP, but that is beyond the scope of the
 * current project). This site therefore forms a more stable, typesafe framework for creating
 * {@link WebScraper}s for use in the application.
 *
 * @author Peter Teixeira
 */
public final class WebScraperFactory {
  private static final Logger logger = LogManager.getLogger();

  public WebScraper forSite(Site site) {
    switch (site) {
      case CAA:
        logger.debug("Creating web scraper for CAA Sports website");
        return new NUWebScraper(new HashMap<>(), new HashMap<>(), new NUDocumentSource());
      default:
        logger.error("Given bad argument {} when creating web scraper", site);
        throw new IllegalArgumentException("Invalid argument when creating WebScraper");
    }
  }
}

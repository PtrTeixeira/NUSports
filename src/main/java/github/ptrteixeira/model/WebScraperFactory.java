package github.ptrteixeira.model;

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
  public WebScraper forSite(Site site) {
    switch (site) {
      case CAA:
        return new NUWebScraper();
      default:
        // Included because Intellij doesn't like switch statements with only a single
        // branch.
        throw new IllegalStateException();
    }
  }
}

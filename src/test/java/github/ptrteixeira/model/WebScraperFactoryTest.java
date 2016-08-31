package github.ptrteixeira.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Peter Teixeira
 */
public class WebScraperFactoryTest {
  private WebScraperFactory factory;

  @BeforeEach
  public void setFactory() {
    this.factory = new WebScraperFactory();
  }

  @Test
  public void forSiteThrowsErrorOnBadArgument() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> factory.forSite(null));
  }

  @Test
  public void forSiteCAACreatesNUWebScraper() {
    // I will be the first to say that this is a bad test.
    // But it is a test, which is a start.
    WebScraper scraper = factory.forSite(Site.CAA);

    assertThat(scraper)
        .isInstanceOf(NUWebScraper.class);
  }
}
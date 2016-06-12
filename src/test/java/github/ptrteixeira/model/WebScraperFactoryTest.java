package github.ptrteixeira.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Peter Teixeira
 */
public class WebScraperFactoryTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void forSiteThrowsErrorOnBadArgument() {
    WebScraperFactory factory = new WebScraperFactory();
    expectedException.expect(IllegalArgumentException.class);
    factory.forSite(null);
  }

  @Test
  public void forSiteCAACreatesNUWebScraper() {
    // I will be the first to say that this is a bad test.
    // But it is a test, which is a start.
    WebScraperFactory factory = new WebScraperFactory();
    WebScraper scraper = factory.forSite(Site.CAA);

    assertThat(scraper, is(instanceOf(NUWebScraper.class)));
  }
}
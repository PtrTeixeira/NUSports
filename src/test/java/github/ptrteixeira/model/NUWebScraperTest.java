package github.ptrteixeira.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNot.not;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Peter Teixeira
 */
public class NUWebScraperTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testClearCacheRemovesStatedElementFromCache() throws Exception {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    standingsCache.put("key1", FXCollections.emptyObservableList());
    scheduleCache.put("key1", FXCollections.emptyObservableList());
    standingsCache.put("key2", FXCollections.emptyObservableList());
    scheduleCache.put("key2", FXCollections.emptyObservableList());


    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    assertThat(standingsCache.keySet(), is(not(empty())));
    assertThat(scheduleCache.keySet(), is(not(empty())));

    webScraper.clearCache("key1");

    assertThat(standingsCache.keySet(), contains("key2"));
    assertThat(scheduleCache.keySet(), contains("key2"));
  }

  @Test
  public void testClearCacheOnNotPresentItemIsNoOp() {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    standingsCache.put("key1", FXCollections.emptyObservableList());
    scheduleCache.put("key1", FXCollections.emptyObservableList());


    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    assertThat(standingsCache.keySet(), is(not(empty())));
    assertThat(scheduleCache.keySet(), is(not(empty())));

    webScraper.clearCache("key2");

    assertThat(standingsCache.keySet(), contains("key1"));
    assertThat(scheduleCache.keySet(), contains("key1"));
  }

  @Test
  public void testClearCacheWithNullArgumentThrowsNullPointerException() {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    expectedException.expect(NullPointerException.class);
    webScraper.clearCache(null);
  }

  @Test
  public void testIfCacheContainsKeyCacheWillBeUsed() throws Exception {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    Standing standing = new Standing("Team 1", "Conference 1", "Standing 1");
    Match match = new Match("Date 1", "Opponent 1", "Result 1");

    standingsCache.put("key1", FXCollections.observableArrayList(standing));
    scheduleCache.put("key1", FXCollections.observableArrayList(match));

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    assertThat(webScraper.getSchedule("key1"), contains(match));
    assertThat(webScraper.getStandings("key1"), contains(standing));
  }

  @Test
  public void testIfCacheDoesNotContainKeyQueryWillBeExecuted() throws Exception {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    expectedException.expect(NullPointerException.class);
    webScraper.getSchedule("key1");
  }

  @Test
  public void getScheduleOnItemNotInCacheParsesAndStoresInCache() throws Exception {
    DocumentSource documentSource = url ->
        Jsoup.parse(new File("src/test/resources/test_schedule.html"), "UTF8", ".");

    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache,
        scheduleCache, documentSource);

    ObservableList<Match> results = webScraper.getSchedule("Baseball");
    assertThat(results, is(not(nullValue())));
    assertThat(results, hasSize(1));
    assertThat(results, contains(
        both(hasProperty("opponent", is("Oklahoma")))
            .and(hasProperty("result", is("W 3 - 2")))));

    assertThat(scheduleCache.keySet(), contains("Baseball"));
    assertThat(scheduleCache.get("Baseball"), hasSize(1));

    assertThat(standingsCache.keySet(), is(empty()));
  }

  @Test
  public void getStandingsOnItemNotInCacheParsesAndStoresInCache() throws Exception {
    DocumentSource documentSource = url ->
        Jsoup.parse(new File("src/test/resources/test_standings.html"), "UTF8", ".");

    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache,
        scheduleCache, documentSource);

    ObservableList<Standing> results = webScraper.getStandings("Baseball");
    System.out.println(results);

    assertThat(results, is(not(nullValue())));
    assertThat(results, contains(
        hasProperty("teamName", equalTo("UNCW")),
        hasProperty("teamName", equalTo("William & Mary")),
        hasProperty("teamName", equalTo("Elon")),
        hasProperty("teamName", equalTo("James Madison")),
        hasProperty("teamName", equalTo("Northeastern")),
        hasProperty("teamName", equalTo("Charleston")),
        hasProperty("teamName", equalTo("Delaware")),
        hasProperty("teamName", equalTo("Towson")),
        hasProperty("teamName", equalTo("Hofstra"))
    ));

    assertThat(standingsCache.keySet(), contains("Baseball"));
    assertThat(standingsCache.get("Baseball"), hasSize(9));

    assertThat(scheduleCache.keySet(), is(empty()));
  }

  @Test
  public void testGetSelectableSports() throws Exception {
    WebScraper webScraper = new NUWebScraper(null, null, null);

    assertThat(webScraper.getSelectableSports(), containsInAnyOrder(
        "Baseball", "Men's Basketball", "Women's Basketball",
        "Volleyball", "Men's Soccer", "Women's Soccer"
    ));
  }

  @Test
  public void testConnectionFailureExceptionThrownOnIOFailureInGetStandings()
      throws Exception {
    DocumentSource documentSource = url -> {
      throw new IOException();
    };
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache,
        scheduleCache, documentSource);

    expectedException.expect(ConnectionFailureException.class);
    webScraper.getStandings("Baseball");
  }

  @Test
  public void testConnectionFailureExceptionThrownOnIOFailureInGetSchedule()
      throws Exception {
    DocumentSource documentSource = url -> {
      throw new IOException();
    };
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache,
        scheduleCache, documentSource);

    expectedException.expect(ConnectionFailureException.class);
    webScraper.getSchedule("Baseball");
  }

}
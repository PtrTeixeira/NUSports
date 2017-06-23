/*
 * Copyright (c) 2017 Peter Teixeira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.ptrteixeira.nusports.model;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

/** @author Peter Teixeira */
public class NUWebScraperTest {
  @Test
  public void testClearCacheRemovesStatedElementFromCache() throws Exception {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    standingsCache.put("key1", FXCollections.emptyObservableList());
    scheduleCache.put("key1", FXCollections.emptyObservableList());
    standingsCache.put("key2", FXCollections.emptyObservableList());
    scheduleCache.put("key2", FXCollections.emptyObservableList());

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    assertThat(standingsCache).containsOnlyKeys("key1", "key2");
    assertThat(scheduleCache).containsOnlyKeys("key1", "key2");

    webScraper.clearCache("key1");

    assertThat(standingsCache).containsOnlyKeys("key2");
    assertThat(scheduleCache).containsOnlyKeys("key2");
  }

  @Test
  public void testClearCacheOnNotPresentItemIsNoOp() {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    standingsCache.put("key1", FXCollections.emptyObservableList());
    scheduleCache.put("key1", FXCollections.emptyObservableList());

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    assertThat(standingsCache).containsOnlyKeys("key1");
    assertThat(scheduleCache).containsOnlyKeys("key1");

    webScraper.clearCache("key2");

    assertThat(standingsCache).containsOnlyKeys("key1");
    assertThat(scheduleCache).containsOnlyKeys("key1");
  }

  @Test
  public void testClearCacheWithNullArgumentThrowsNullPointerException() {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> webScraper.clearCache(null));
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

    assertThat(webScraper.getSchedule("key1")).contains(match);
    assertThat(webScraper.getStandings("key1")).contains(standing);
  }

  @Test
  public void testIfCacheDoesNotContainKeyQueryWillBeExecuted() throws Exception {
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, null);

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> webScraper.getSchedule("key1"));
  }

  @Test
  public void getScheduleOnItemNotInCacheParsesAndStoresInCache() throws Exception {
    DocumentSource documentSource =
        url -> Jsoup.parse(new File("src/test/resources/test_schedule.html"), "UTF8", ".");

    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, documentSource);

    assertThat(webScraper.getSchedule("Baseball"))
        .isNotNull()
        .hasSize(1)
        .extracting(Match::getOpponent, Match::getResult)
        .containsExactly(tuple("Oklahoma", "W 3 - 2"));

    assertThat(scheduleCache).containsOnlyKeys("Baseball");
    assertThat(scheduleCache.get("Baseball")).hasSize(1);
    assertThat(standingsCache).isEmpty();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getStandingsOnItemNotInCacheParsesAndStoresInCache() throws Exception {
    DocumentSource documentSource =
        url -> Jsoup.parse(new File("src/test/resources/test_standings.html"), "UTF8", ".");

    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, documentSource);

    assertThat(webScraper.getStandings("Baseball"))
        .isNotNull()
        .extracting(Standing::getTeamName)
        .containsExactly(
            "UNCW",
            "William & Mary",
            "Elon",
            "James Madison",
            "Northeastern",
            "Charleston",
            "Delaware",
            "Towson",
            "Hofstra");

    assertThat(standingsCache).containsOnlyKeys("Baseball");
    assertThat(standingsCache.get("Baseball")).hasSize(9);
    assertThat(scheduleCache).isEmpty();
  }

  @Test
  public void testGetSelectableSports() throws Exception {
    WebScraper webScraper = new NUWebScraper(null, null, null);

    assertThat(webScraper.getSelectableSports())
        .containsExactlyInAnyOrder(
            "Baseball", "Softball",
            "Volleyball", "Field Hockey",
            "Men's Basketball", "Women's Basketball",
            "Men's Soccer", "Women's Soccer");
  }

  @Test
  public void testConnectionFailureExceptionThrownOnIoFailureInGetStandings() throws Exception {
    DocumentSource documentSource =
        url -> {
          throw new IOException();
        };
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, documentSource);

    assertThatExceptionOfType(ConnectionFailureException.class)
        .isThrownBy(() -> webScraper.getStandings("Baseball"));
  }

  @Test
  public void testConnectionFailureExceptionThrownOnIoFailureInGetSchedule() throws Exception {
    DocumentSource documentSource =
        url -> {
          throw new IOException();
        };
    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, documentSource);

    assertThatExceptionOfType(ConnectionFailureException.class)
        .isThrownBy(() -> webScraper.getSchedule("Baseball"));
  }

  @Test
  public void testCorrectlyParsesResultsWhenGameNotYetPlayed() throws Exception {
    DocumentSource source =
        url ->
            Jsoup.parse(
                new File("src/test/resources/test_schedule_unplayed_games.html"), "UTF8", ".");

    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, source);

    assertThat(webScraper.getSchedule("Baseball"))
        .extracting(Match::getResult)
        .hasSize(1)
        .containsExactly("");
  }

  @Test
  public void testCorrectlyParsesResultInCaseOfTie() throws Exception {
    DocumentSource source =
        url -> Jsoup.parse(new File("src/test/resources/test_schedule_ties.html"), "UTF8", ".");

    HashMap<String, ObservableList<Standing>> standingsCache = new HashMap<>();
    HashMap<String, ObservableList<Match>> scheduleCache = new HashMap<>();

    WebScraper webScraper = new NUWebScraper(standingsCache, scheduleCache, source);

    assertThat(webScraper.getSchedule("Baseball"))
        .extracting(Match::getResult)
        .hasSize(1)
        .containsExactly("2 - 2");
  }
}

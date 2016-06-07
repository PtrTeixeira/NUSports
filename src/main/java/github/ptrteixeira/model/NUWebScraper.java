package github.ptrteixeira.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * Scrape the CAA site, in particular, to get information relevant to
 * Northeastern.
 *
 * It is extremely tightly tied to the actual structure of the CAA site, but I
 * couldn't find a REST endpoint or anything similar that would allow me to
 * trivially extract the information that I needed. So it just scrapes straight
 * off of HTML, which works until the CAA changes how their site is laid out
 * again.
 *
 * @author Peter
 */
final class NUWebScraper implements WebScraper {
  private static final Logger logger = LogManager.getLogger();

  private final Map<String, ObservableList<Standing>> standingsCache;
  private final Map<String, ObservableList<Match>> scheduleCache;

  NUWebScraper() {
    this.standingsCache = new HashMap<>();
    this.scheduleCache = new HashMap<>();
  }

  @Override
  public void clearCache(String sport) {
    logger.info("Clearing the cache for \"{}\"", sport);
    this.scheduleCache.remove(sport);
    this.standingsCache.remove(sport);
  }

  @Override
  public ObservableList<Standing> getStandings(String sport) throws ConnectionFailureException {
    if (standingsCache.containsKey(sport)) {
      logger.debug("Using cache to get standings for \"{}\"", sport);
      return standingsCache.get(sport);
    }

    try {
      logger.debug("Standings for \"{}\" not found in cache; connecting to internet.", sport);
      ObservableList<Standing> data = FXCollections.observableArrayList();
      String queryPath = "http://caasports.com/standings.aspx?path=" + this.sportToPath(sport);
      logger.debug("Making query to path {}", queryPath);
      Document doc = Jsoup.connect(queryPath)
          .userAgent("Chrome/51")
          .get();

      Elements rows = doc.getElementsByClass("default_dgrd") // list of <table>
          .first() // <table>
          .children() // list of <tbody>
          .first() // <tbody>
          .children();                          // list of <tr>
      rows.remove(0);                               // drop header

      rows.stream().map(standingsParser(sport)).forEach(data::add);

      logger.debug("Found standings data on the web for \"{}\". Writing to cache.", sport);
      this.standingsCache.put(sport, data);
      return data;
    } catch (IOException e) {
      logger.warn("Connection failure getting standings data", e);
      throw new ConnectionFailureException("Failed to connect to the internet.");
    }
  }

  @Override
  public ObservableList<Match> getSchedule(String sport) throws ConnectionFailureException {
    if (scheduleCache.containsKey(sport)) {
      logger.info("Using cache to get schedule for \"{}\"", sport);
      return scheduleCache.get(sport);
    }

    try {
      logger.debug("Schedule for \"{}\" not found in cache; connecting to internet", sport);
      ObservableList<Match> data = FXCollections.observableArrayList();
      String queryPath = "http://caasports.com/calendar.aspx";
      logger.debug("Making query to path {}", queryPath);
      Document doc = Jsoup.connect(queryPath)
          .header("Connection", "keep-alive")
          .header("Accept-Encoding", "gzip, deflate, sdch")
          .userAgent("Chrome/51")
          .maxBodySize(0)
          .timeout(7000)
          .get();

      Elements nuGames = this.extractSport(doc.getElementsByClass("sport_3"), sport);

      nuGames.stream().map(this::parseMatch).forEach(data::add);

      logger.debug("Found schedule data on the web for {}. Writing to cache.", sport);
      this.scheduleCache.put(sport, data);
      return data;
    } catch (IOException e) {
      logger.trace("Connection failure getting schedule data", e.fillInStackTrace());
      throw new ConnectionFailureException("Failed to connect to the internet.");
    }
  }

  @Override
  public List<String> getSelectableSports() {
    return Collections.singletonList("Men's Basketball");
  }

  // In general, the standings tables look like
  // Hofstra | 0 - 12 | 0.000 | 5 - 25 | 0.2000
  // So this just grabs the correct elements.
  // Parse a generic standing table row into a Standing object
  private Standing parseNormalStanding(Element e) {
    return new Standing(
        e.child(0).text(), // School
        e.child(1).text(), // Conference Results
        e.child(3).text());           // Overall Results
  }

    // In contrast, soccer standings look like 
  // Hofstra | 0-12 | 0.000 | 0 | 5 - 25 | 0.200 | 15
  // Where the extra elements are points. So this just corrects for the 
  // change.
  // Parse a soccer standing table row into a Standing object
  private Standing parseSoccerStanding(Element e) {
    return new Standing(
        e.child(0).text(), // School
        e.child(1).text(), // Conference Results
        e.child(4).text());// Overall results
  }

  private Function<Element, Standing> standingsParser(String sport) {
    if (sport.equals("Men's Soccer") || sport.equals("Women's Soccer")) {
      return this::parseSoccerStanding;
    } else {
      return this::parseNormalStanding;
    }
  }

  // Parse the table row in the document into a Match
  private Match parseMatch(Element e) {
    String opponent;
    int northeasternIndex;
    if (e.child(1).text().equals("Northeastern")) {
      opponent = e.child(4).text();
      northeasternIndex = 1;
    } else {
      opponent = e.child(1).text();
      northeasternIndex = 4;
    }

    String result;
    if (e.child(northeasternIndex).hasClass("won")) {
      result = MessageFormat.format("{} {} - {}", "W", e.child(2).text(), e.child(5).text());
    } else {
      result = MessageFormat.format("{} {} - {}", "L", e.child(2).text(), e.child(5).text());
    }

    String date = this.getDate(e);

    return new Match(date, opponent, result);
  }

  // Look up through the table until it hits a row that contains the date
  private String getDate(Element e) {
    while (e != null && !e.hasAttr("data-date")) {
      if (e.previousElementSibling() != null) {
        e = e.previousElementSibling();
      }
    }

    if (e != null && e.hasAttr("data-date")) {
      return e.attr("data-date");
    } else {
      return "";
    }
  }

  // Extract all elements in e1 with a class of sport
  private Elements extractSport(Elements el, String sport) {
    sport = "." + this.sportToClass(sport);

    return el.select(sport);
  }

    // Convert the given string sport into a url sport path
  // Called in generating standings tables
  private String sportToPath(String sport) {
    logger.trace("Getting URL path for \"{}\"", sport);

    switch (sport) {
      case "Baseball":
        return "baseball";
      case "Men's Basketball":
        return "mbball";
      case "Men's Soccer":
        return "msoc";
      case "Women's Basketball":
        return "wbball";
      case "Women's Soccer":
        return "wsoc";
      case "Volleyball":
        return "wvball";
      default:
        return "";
    }
  }

    // Convert the given string sport into an HTML class 
  // Used in extracting data from the calendar
  private String sportToClass(String sport) {
    logger.debug("Finding CSS class for \"{}\"", sport);

    switch (sport) {
      case "Baseball":
        return "sport_1";
      case "Men's Basketball":
        return "sport_6";
      case "Men's Soccer":
        return "sport_8";
      case "Women's Basketball":
        return "sport_13";
      case "Women's Soccer":
        return "sport_16";
      case "Volleyball":
        return "sport_17";
      default:
        return "";
    }

    /*
     * class school_3 = Northeastern
     *
     * class sport_1 = Baseball
     * class sport_3 = Field Hockey
     * class sport_4 = Football
     * class sport_6 = Men's Basketball
     * class sport_18 = Men's XC
     * class sport_20 = Men's Golf
     * class sport_7 = Men's Lacrosse
     * class sport_8 = Men's Soccer
     * class sport_22 = Men's Swimming
     * class sport_24 = Men's Tennis
     * class sport_26 = Men's Track and Field
     * class sport_9 = Softball
     * class sport_17 = Volleyball
     * class sport_13 = Women's Basketball
     * class sport_19 = Women's XC
     * class sport_21 = Women's Golf
     * class sport_14 = Women's Lacrosse
     * class sport_16 = Women's Soccer
     * class sport_23 = Women's Swimming
     * class sport_25 = Women's Tennis
     * class sport_27 = Women's Track and Field
     * class sport_28 = Wrestling
     */
  }
}

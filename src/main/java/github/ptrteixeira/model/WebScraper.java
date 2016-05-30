package github.ptrteixeira.model;

import javafx.collections.ObservableList;

/**
 * Public API of WebScraper objects.
 *
 * @author Peter
 * @version 0.1
 */
public interface WebScraper {

  /**
   * Get the standings (ie, win/loss records) for the given sport.
   *
   * @param sport String label of what sport to get
   * @return A list of the Standings for that sport
   * @throws ConnectionFailureException if the site this scraper is attached to cannot be reached
   */
  ObservableList<Standing> getStandings(String sport) throws ConnectionFailureException;

  /**
   * Get the schedule for a specific team in the given sport.
   * <p>
   * For this particular project, the team (Northeastern) is dictated as
   * part of the definition of the web scraper. It may be better to extend
   * this so that it takes the team name and the name of the sport.
   *
   * @param sport String label of what sport to get
   * @return A list of Matches for that sport
   * @throws ConnectionFailureException if the site this scraper is attached to cannot be reached
   */
  ObservableList<Match> getSchedule(String sport) throws ConnectionFailureException;

  /**
   * Clear the cache for the given sport, typically to force an update.
   *
   * @param sport String label of what sport to clear the cache of.
   */
  void clearCache(String sport);
}

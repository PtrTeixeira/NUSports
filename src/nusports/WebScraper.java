package nusports;

import javafx.collections.ObservableList;

/**
 * Public API of WebScraper objects.
 * 
 * @author Peter
 * @version 0.1
 */
interface WebScraper {
    
    /**
     * Get the standings (ie, win/loss records) for the given sport.
     * 
     * @param sport String label of what sport to get
     * @return A list of the Standings for that sport
     */
    ObservableList<Standing> getStandings(String sport);
    
    /**
     * Get the schedule for a specific team in the given sport.
     * 
     * For this particular project, the team (Northeastern) is dictated as 
     * part of the definition of the web scraper. It may be better to extend 
     * this so that it takes the team name and the name of the sport.
     * 
     * @param sport String label of what sport to get
     * @return A list of Matches for that sport
     */
    ObservableList<Match> getSchedule(String sport);
    
    /**
     * Clear the cache for the given sport, typically to force an update.
     * 
     * @param sport String label of what sport to clear the cache of.
     */
    void clearCache(String sport);
}

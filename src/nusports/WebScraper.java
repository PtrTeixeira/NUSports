package nusports;

import javafx.collections.ObservableList;

/**
 *
 * @author Peter
 * @version 0.1
 */
public interface WebScraper {
    
    // Returns an ObservableList of the standings for the given sport
    ObservableList getStandings(String sport);
    
    // Returns an ObservableList of the schedule for the given sport
    ObservableList getSchedule(String sport);
    
    // Force a reload of page content
    void clearCache();
}

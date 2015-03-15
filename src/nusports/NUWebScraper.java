package nusports;

import java.io.IOException;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
* TODO: 
*   Caching
*/

public class NUWebScraper implements WebScraper {
    private HashMap<String, ObservableList> standingsCache;
    private HashMap<String, ObservableList> scheduleCache;
    
    private final OutputGenerator caller; 
    
    public NUWebScraper(OutputGenerator caller) {
        this.caller = caller;
        this.standingsCache = new HashMap<>();
        this.scheduleCache = new HashMap<>();
    }
    
    // Method stub
    public void clearCache() {
        this.scheduleCache.clear();
        this.standingsCache.clear();
    }
    
    // Return an observableList of the NU's standings.
    public ObservableList getStandings(String sport) {
        Document doc = null; // Explicitly set to null
        Elements rows;
        ObservableList data = FXCollections.observableArrayList();
        
        
        if (standingsCache.containsKey(sport)) {
            data = standingsCache.get(sport);
        }
        else {
            try {
                doc = Jsoup.connect(
                    "http://caasports.com/standings.aspx?path=" + 
                    this.sportToPath(sport)).get();
            }
            catch (IOException e) {
                this.caller.pushToError("Unable to connect to the Internet");
                return data;  // Instantly terminate function call.
            }

            // If the code ever reaches this point, doc is non-null
            rows = doc.getElementsByClass("default_dgrd") // list of <table>
                    .first()                              // <table>
                    .children()                           // list of <tbody>
                    .first()                              // <tbody>
                    .children();                          // list of <tr>
            rows.remove(0);                               // drop header

            if (!sport.equals("Men's Soccer") && 
                !sport.equals("Women's Soccer")) {
                for (Element e : rows) {
                    data.add(parseStanding(e));
                }
            }
            else {
                for (Element e : rows) {
                    data.add(parseSoccerStanding(e));
                }
            }
            standingsCache.put(sport, data);
        }
        this.caller.clearError();
        return data;
    }
    
    // Returns an ObservableList of the schedule of games.
    public ObservableList getSchedule(String sport) {
        Document doc = null; // Explicitly set to null
        Elements nuGames;
        ObservableList data = FXCollections.observableArrayList();
        
        if (scheduleCache.containsKey(sport)) {
            data = scheduleCache.get(sport); // Query cache
        }
        else {
            try {
                doc = Jsoup.connect(
                        "http://caasports.com/calendar.aspx")
                        .header("Connection", "keep-alive")
                        .header("Accept-Encoding", "gzip, deflate, sdch")
                        .maxBodySize(0)
                        .timeout(7000)
                        .get();
            }
            catch (IOException e) {
                this.caller.pushToError("Failed to connect to interwebs.");
                return data;
            }

            // If the code ever reaches this point, doc should not be null
            nuGames = doc.getElementsByClass("school_3");

            nuGames = this.extractSport(nuGames, sport);

            for (Element e : nuGames) {
                data.add(parseMatch(e));
            }
            scheduleCache.put(sport, data); // Add to cache
        }
        this.caller.clearError();
        return data;
    }
    
    // In general, the standings tables look like
    // Hofstra | 0 - 12 | 0.000 | 5 - 25 | 0.2000
    // So this just grabs the correct elements.
    
    // Parse a generic standing table row into a Standing object
    private Standing parseStanding(Element e) {
        Standing retr = new Standing(e.child(0).text(),   // School
                            e.child(1).text(),            // Conference Results
                            e.child(3).text());           // Overall Results

        return retr;
    }
    
    // In contrast, soccer standings look like 
    // Hofstra | 0-12 | 0.000 | 0 | 5 - 25 | 0.200 | 15
    // Where the extra elements are points. So this just corrects for the 
    // change.
    
    // Parse a soccer standing table row into a Standing object
    private Standing parseSoccerStanding(Element e) {
        Standing retr = new Standing(e.child(0).text(), // School
                                     e.child(1).text(), // Conference Results
                                     e.child(4).text());// Overall results
        
        return retr;
    }
    
    // Parse the table row in the document into a Match
    private Match parseMatch(Element e) {
        String opponent = "";
        String result = "";
        String date = "";
        
        if (e.children().size() >= 6) {
            if (e.child(1).text().equals("Northeastern")) {
                opponent = e.child(4).text();
                if (e.child(1).hasClass("won")) {
                    result = "W " + e.child(2).text() + " - " 
                            + e.child(5).text();
                }
                else if (e.child(4).hasClass("won")){
                    result = "L " + e.child(5).text() + " - " 
                            + e.child(2).text();
                }
                else {
                    result = "";
                }
            }
            else {
                opponent = e.child(1).text();
                if (e.child(4).hasClass("won")) {
                    result = "W " + e.child(5).text() + " - " 
                            + e.child(2).text();
                }
                else if (e.child(1).hasClass("won")){
                    result = "L " + e.child(2).text() + " - " 
                            + e.child(5).text();
                }
                else {
                    result = "";
                }
            }
            
            date = this.getDate(e);
        }
        
        
        return new Match(date, opponent, result);
    }
    
    // Look up through the table until it hits a row that contains the date
    private String getDate(Element e) {
        while (e != null && !e.hasAttr("data-date")) {
            if (e.previousElementSibling() != null) {
                e = e.previousElementSibling();
            }
        }
        
        if (e.hasAttr("data-date")) {
            return e.attr("data-date");
        }
        else {
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
        String path;
        
        switch (sport) {
            case "Baseball":
                path = "baseball";
                break;
            case "Men's Basketball":
                path = "mbball";
                break;
            case "Men's Soccer":
                path = "msoc";
                break;
            case "Women's Basketball":
                path = "wbball";
                break;
            case "Women's Soccer":
                path = "wsoc";
                break;
            case "Volleyball":
                path = "wvball";
                break;
            default:
                path = "";
                break;
        }
        
        return path;
    }
    
    // Convert the given string sport into a class 
    // Used in extracting data from the calendar
    private String sportToClass(String sport) {
        String path;
        
        switch (sport) {
            case "Baseball":
                path = "sport_1";
                break;
            case "Men's Basketball":
                path = "sport_6";
                break;
            case "Men's Soccer":
                path = "sport_8";
                break;
            case "Women's Basketball":
                path = "sport_13";
                break;
            case "Women's Soccer":
                path = "sport_16";
                break;
            case "Volleyball":
                path = "sport_17";
                break;
            default:
                path = "";
                break;
        }
        
        return path;
        
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
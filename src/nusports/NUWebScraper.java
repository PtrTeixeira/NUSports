package nusports;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
* TODO: 
*    Caching
*    IOExceptions shouldn't be passed upwards, they should case the page 
*      to attempt to reload
*    This class should be renamed to NUWebScraper, implementing a WebScraper
*      interface
*/

public class NUWebScraper {
    public String cache; // TODO
    
    public NUWebScraper() {
        
    }
    
    // Return an observableList of the NU's standings.
    public ObservableList getStandings(String sport) throws IOException {
        Document doc;
        Elements rows;
        ObservableList data = FXCollections.observableArrayList();
        
        doc = Jsoup.connect(
            "http://caasports.com/standings.aspx?path=" + 
            this.sportToPath(sport)).get();
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
        
        return data;
    }
    
    // In general, the standings tables look like
    // Hofstra | 0 - 12 | 0.000 | 5 - 25 | 0.2000
    // So this just grabs the correct elements.
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
    private Standing parseSoccerStanding(Element e) {
        Standing retr = new Standing(e.child(0).text(), // School
                                     e.child(1).text(), // Conference Results
                                     e.child(4).text());// Overall results
        
        return retr;
    }
    
    
    public ObservableList getSchedule(String sport) throws IOException {
        Document doc = Jsoup.connect(
            "http://caasports.com/calendar.aspx")
                .header("ctl00$cplhMainContent$datesearch_1", "12/4/2014")
                .header("ctl00$cplhMainContent$datesearch_2", "3/5/2015")
                .header("Connection", "keep-alive")
                .get();
        Elements nuGames = doc.getElementsByClass("school_3");
        ObservableList data = FXCollections.observableArrayList();
        
        nuGames = this.extractSport(nuGames, sport);
        
        for (Element e : nuGames) {
            data.add(parseMatch(e));
        }
        
        
        return data;
    }
    
    
    /*
    * parseMatch(Element) -> Match . Accumulate the data into a Match object
    * getDate(Element e) -> String. Look upwards until it finds a row with a 
    *        date
    */
    
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
    
    private Elements extractSport(Elements el, String sport) {
        sport = "." + this.sportToClass(sport);
        
        return el.select(sport);
    }
    
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

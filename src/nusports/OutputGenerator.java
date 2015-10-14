package nusports;

import java.util.ArrayList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

/**
 *
 * @author Peter
 * @version 0.1
 */

// Generates and formats the output. This file takes the ObservableList
// produced the NUWebScraper and pushes it into the output tables.
public class OutputGenerator {
    // Standings table.
    private final TableColumn<Standing,String> teams;     
    private final TableColumn<Standing,String> conference; // conference record
    private final TableColumn<Standing,String> overall;    // overall record
    // Schedule table
    private final TableColumn<Match,String> date;          // Game date
    private final TableColumn<Match,String> opponent;      // Game opponent
    private final TableColumn<Match,String> result;        // Game result
    // The table
    private final TableView table;                         // Table to push to
    // The error console
    private final Text error;                              // Report errors
    // Webscraper to acquire text
    private final WebScraper scraper;                      // Input scraper

    // Constructor.
    public OutputGenerator(TableView table, Text error) {
        teams = new TableColumn("Teams");
        teams.setMinWidth(100);
        teams.setCellValueFactory( 
            new PropertyValueFactory<>("teamName"));
        
        conference = new TableColumn("CAA");
        conference.setMinWidth(100);
        conference.setCellValueFactory(
            new PropertyValueFactory<>("conference"));
        
        overall = new TableColumn("Overall");
        overall.setMinWidth(100);
        overall.setCellValueFactory(
            new PropertyValueFactory<>("Overall"));
        
        date = new TableColumn("Date");
        date.setMinWidth(100);
        date.setCellValueFactory(
            new PropertyValueFactory<>("date"));
        
        opponent = new TableColumn("Opponent");
        opponent.setMinWidth(100);
        opponent.setCellValueFactory(
            new PropertyValueFactory<>("opponent"));
        
        result = new TableColumn("Result");
        result.setMinWidth(100);
        result.setCellValueFactory(
            new PropertyValueFactory<>("result"));
        
        this.table = table;
        this.error = error;
        this.scraper = new NUWebScraper(this);
    }
    
    // Called by clients to push results to table.
    public void resetTable(String sport, String option) {
        setCols(option);
        setData(sport, option);
    }
    
    // Push the given string to the error console
    public void pushToError(String error) {
        this.error.setText(error);
    }
    
    // Clear the error console
    public void clearError() {
        this.error.setText("");
    }

    // Called to change column headings. Specifically, pushes the standings
    // headings if option is for standings and the schedule headings if the 
    // option is for schedules.
    private void setCols(String option) {
        ArrayList<TableColumn> cols = new ArrayList<>();
        
        if (option.equals("League Standings")) {
            cols.add(teams);
            cols.add(conference);
            cols.add(overall);
        }
        else if (option.equals("Schedule/Results")) {
            cols.add(date);
            cols.add(opponent);
            cols.add(result);
        }
        else {
            cols.add(new TableColumn("Sports!"));
        }
        
        table.getColumns().setAll(cols);
    }
    
    // tells the scraper to do its thing.
    // TODO : second conditional block needs to be changed 
    // based on edits to the scraper
    private void setData(String sport, String options) {
        if (options.equals("League Standings")) {
            table.setItems(scraper.getStandings(sport));
        }
        else if (options.equals("Schedule/Results")) {
            table.setItems(scraper.getSchedule(sport));
        }
    }
    
    // reload the current page based on user request
    public void reloadCurrent(String sport, String options) {
        this.scraper.clearCache(sport);
        this.resetTable(sport, options);
    }
}

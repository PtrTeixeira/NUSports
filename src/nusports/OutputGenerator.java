package nusports;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
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
    private TableColumn<Standing,String> teams;         // teams in conference
    private TableColumn<Standing,String> conference;    // results in conference
    private TableColumn<Standing,String> overall;       // overall results
    
    // Schedule table
    private TableColumn<Match,String> date;             // Game date
    private TableColumn<Match,String> opponent;         // Game opponent
    private TableColumn<Match,String> result;           // Game result
    
    // The table
    private TableView table;                            // Table to push to
    
    private Text error;                                   // Place to put errors
    
    // uses the WebScraper interface
    private WebScraper scraper;                         // Scraper to get input

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
    
    public void pushToError(String error) {
        this.error.setText(error);
        this.error.setFill(Color.RED);
    }
    
    public void clearError() {
        this.error.setText("");
    }
    
    
}

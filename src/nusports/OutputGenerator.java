package nusports;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    
    private TableView table;                            // Table to push to
    
    private NUWebScraper scraper;                         // Scraper to get input
    
    
    public void setData(TableView table) {
        ObservableList data = 
                FXCollections.observableArrayList(
                new Standing("Northeastern", "0-0", "0-0"), 
                new Standing("William & Mary", "0-0", "0-0"));
        
        table.setItems(data);
    }
    
    // Constructor.
    public OutputGenerator(TableView table) {
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
        
        this.scraper = new NUWebScraper();
    }
    
    // Called by clients to push results to table.
    public void resetTable(String sport, String option) {
        setCols(option);
        setData(sport, option);
    }
    
    // Deprecated.
    private void setBaseballStandings() {
        ObservableList data = 
                FXCollections.observableArrayList(
                new Standing("Northeastern", "0-0", "0-0"), 
                new Standing("William & Mary", "0-0", "0-0"));
        
        table.setItems(data);
    }
    
    // Soon to be deprecated.
    private void setBaseballSchedule() {
        ObservableList data = 
                FXCollections.observableArrayList(
                new Match("13 Feb 2015", "At James Madison", "7-3"), 
                new Match("14 Feb 2015", "Vs. Bucknell", "3-2"));
        
        table.setItems(data);
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
    
    
    
    
}

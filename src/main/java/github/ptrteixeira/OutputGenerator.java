package github.ptrteixeira;

import github.ptrteixeira.model.*;
import github.ptrteixeira.view.DisplayType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Generates and formats the output to the JavaFX front-end.
 *
 * Takes the ObservableList produced by a WebScraper and pushes it into the
 * output tables. This is, loosely speaking, the view, but I made this when I
 * had no idea what an MVC architecture was. Given that, I think I did
 * reasonably well at separation of concerns.
 *
 * @author Peter
 * @version 0.1
 */
class OutputGenerator {

  // Standings table.

  private final TableColumn<Standing, String> teams;
  private final TableColumn<Standing, String> conference; // conference record
  private final TableColumn<Standing, String> overall;    // overall record
  // Schedule table
  private final TableColumn<Match, String> date;          // Game date
  private final TableColumn<Match, String> opponent;      // Game opponent
  private final TableColumn<Match, String> result;        // Game result
  // The table
  private final TableView table;                         // Table to push to
  // The error console
  private final Text error;                              // Report errors
  // Webscraper to acquire text
  private final WebScraper scraper;                      // Input scraper

  /**
   * Sets up the targets for injection.
   *
   * Specifically, initializes the headers on the TableView and provides a way
   * for the model to inject error reports onto the front-end.
   *
   * @param table TableView from the front-end, for injection
   * @param error Text field from the front-end, for injection
   */
  public OutputGenerator(TableView table, Text error) {
    teams = new TableColumn<>("Teams");
    teams.setMinWidth(100);
    teams.setCellValueFactory(
        new PropertyValueFactory<>("teamName"));

    conference = new TableColumn<>("CAA");
    conference.setMinWidth(100);
    conference.setCellValueFactory(
        new PropertyValueFactory<>("conference"));

    overall = new TableColumn<>("Overall");
    overall.setMinWidth(100);
    overall.setCellValueFactory(
        new PropertyValueFactory<>("Overall"));

    date = new TableColumn<>("Date");
    date.setMinWidth(100);
    date.setCellValueFactory(
        new PropertyValueFactory<>("date"));

    opponent = new TableColumn<>("Opponent");
    opponent.setMinWidth(100);
    opponent.setCellValueFactory(
        new PropertyValueFactory<>("opponent"));

    result = new TableColumn<>("Result");
    result.setMinWidth(100);
    result.setCellValueFactory(
        new PropertyValueFactory<>("result"));

    this.table = table;
    this.error = error;
    this.scraper = new WebScraperFactory().forSite(Site.CAA);
  }

  /**
   * Called by clients to push results to table.
   *
   * Clears current values in the table and puts in new ones.
   *
   * In some sense, this is where my idea of separation of concerns breaks down.
   * The ideal would be for the model to know nothing about these functions, and
   * the controller would handle interpreting responses from the Model. But hey.
   *
   * @param sport Sport to reset
   * @param option "League Standings" or "Schedule/Results"
   */
  public void resetTable(String sport, DisplayType option) {
    setCols(option);
    setData(sport, option);
  }

  /**
   * Print the given string to the error console on the front-end.
   *
   * @param error String to print to error console.
   */
  public void pushToError(String error) {
    this.error.setText(error);
  }

  private void clearError() {
    this.error.setText("");
  }

  // Called to change column headings. Specifically, pushes the standings
  // headings if option is for standings and the schedule headings if the 
  // option is for schedules.
  private void setCols(DisplayType option) {
    ArrayList<TableColumn> cols = new ArrayList<>();
    switch (option) {
      case SCHEDULE:
        cols.add(teams);
        cols.add(conference);
        cols.add(overall);
        break;
      case STANDINGS:
        cols.add(date);
        cols.add(opponent);
        cols.add(result);
    }

    table.getColumns().setAll(cols);
  }

  // tells the scraper to do its thing.
  private void setData(String sport, DisplayType options) {
    this.clearError();
    try {
      switch (options) {
        case SCHEDULE:
          table.setItems(scraper.getSchedule(sport));
          break;
        case STANDINGS:
          table.setItems(scraper.getStandings(sport));
      }
    } catch (ConnectionFailureException cfx) {
      this.error.setText(cfx.getMessage());
    }
  }

  /**
   * Reload the current page.
   * 
   * Only fired in the event that the user explicitly requests that the page 
   * be reloaded; otherwise, the program only reloads when you flip between 
   * different screens.
   * 
   * @param sport Sport to look at
   * @param options Type of view to be displayed
   */
  public void reloadCurrent(String sport, DisplayType options) {
    this.scraper.clearCache(sport);
    this.resetTable(sport, options);
  }
}

package github.ptrteixeira.view;

import github.ptrteixeira.model.Standing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Peter Teixeira
 */
final class StandingsTab extends AbstractViewTab<Standing> {
  private static final Logger logger = LogManager.getLogger();
  private ObservableList<Standing> tableContents;

  StandingsTab() {
    super();
    this.setText("Standings");
    logger.trace("Created Standings tab.");
  }

  @Override
  final Node createCenter() {
    this.tableContents = FXCollections.observableArrayList();

    TableView<Standing> tableView = new TableView<>(this.tableContents);
    TableColumn<Standing, String> teamName = new TableColumn<>("Team");
    teamName.setCellValueFactory(new PropertyValueFactory<>("teamName"));
    TableColumn<Standing, String> conference = new TableColumn<>("Conference");
    conference.setCellValueFactory(new PropertyValueFactory<>("conference"));
    TableColumn<Standing, String> overall = new TableColumn<>("Result");
    overall.setCellValueFactory(new PropertyValueFactory<>("result"));

    tableView.getColumns().add(teamName);
    tableView.getColumns().add(conference);
    tableView.getColumns().add(overall);

    return tableView;
  }

  @Override
  void populateTable(List<Standing> contents) {
    logger.trace("Set contents of Standings table to {}", contents);
   this.tableContents.setAll(contents);
  }
}

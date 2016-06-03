package github.ptrteixeira.view;

import github.ptrteixeira.model.Match;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Peter Teixeira
 */
final class ScheduleTab extends AbstractViewTab<Match> {
  private static final Logger logger = LogManager.getLogger();

  private final TableView<Match> tableView;

  ScheduleTab() {
    super();
    this.setText("Schedule");
    this.tableView = new TableView<>();
    logger.trace("Created Schedule tab.");
  }

  @Override
  Node createCenter() {
    return this.tableView;
  }

  @Override
  void populateTable(List<Match> contents) {
    logger.trace("Set contents of Schedule table.");
    this.tableView.setItems(FXCollections.observableList(contents));
  }
}

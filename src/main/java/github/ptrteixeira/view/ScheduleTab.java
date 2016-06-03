package github.ptrteixeira.view;

import github.ptrteixeira.model.Match;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * @author Peter Teixeira
 */
final class ScheduleTab extends AbstractViewTab<Match> {
  private final TableView<Match> tableView;

  ScheduleTab() {
    super();
    this.setText("Schedule");
    this.tableView = new TableView<>();
  }

  @Override
  Node createCenter() {
    return this.tableView;
  }

  @Override
  void populateTable(List<Match> contents) {
    this.tableView.setItems(FXCollections.observableList(contents));
  }
}

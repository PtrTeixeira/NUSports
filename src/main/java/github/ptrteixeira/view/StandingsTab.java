package github.ptrteixeira.view;

import github.ptrteixeira.model.Standing;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * @author Peter Teixeira
 */
final class StandingsTab extends AbstractViewTab<Standing> {
  private final TableView<Standing> tableView;

  StandingsTab() {
    super();
    this.setText("Standings");
    this.tableView = new TableView<>();
  }

  @Override
  final Node createCenter() {
    return this.tableView;
  }

  @Override
  void populateTable(List<Standing> contents) {
   this.tableView.setItems(FXCollections.observableList(contents));
  }
}

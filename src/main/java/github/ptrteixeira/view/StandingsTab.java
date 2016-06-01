package github.ptrteixeira.view;

import github.ptrteixeira.model.Standing;
import javafx.scene.Node;
import javafx.scene.control.TableView;

/**
 * @author Peter Teixeira
 */
final class StandingsTab extends AbstractViewTab {
  StandingsTab() {
    super();
    this.setText("Standings");
  }

  @Override
  final Node createCenter() {
    return new TableView<Standing>();
  }
}

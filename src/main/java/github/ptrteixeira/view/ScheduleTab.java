package github.ptrteixeira.view;

import github.ptrteixeira.model.Match;
import javafx.scene.Node;
import javafx.scene.control.TableView;

/**
 * @author Peter Teixeira
 */
final class ScheduleTab extends AbstractViewTab<Match> {
  ScheduleTab() {
    super();
    this.setText("Schedule");
  }

  @Override
  Node createCenter() {
    return new TableView<Match>();
  }
}

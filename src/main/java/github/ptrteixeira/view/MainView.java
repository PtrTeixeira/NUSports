package github.ptrteixeira.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class MainView {

  public Pane createView() {
    BorderPane pane = new BorderPane();
    pane.setCenter(this.createCenter());
    pane.setBottom(this.createBottomBar());

    pane.setMinWidth(600);
    pane.setMinHeight(600);

    return pane;
  }

  private Node createBottomBar() {
    HBox bottomBar = new HBox();
    bottomBar.setAlignment(Pos.CENTER_RIGHT);
    Button reload= new Button("Reload");

    bottomBar.getChildren().add(reload);

    return bottomBar;
  }

  private Node createCenter() {
    TabPane tabPane = new TabPane();
    tabPane.getTabs()
        .addAll(new ScheduleTab(), new StandingsTab());

    return tabPane;
  }
}

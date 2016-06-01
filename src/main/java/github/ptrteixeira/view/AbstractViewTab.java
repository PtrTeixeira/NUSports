package github.ptrteixeira.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Collections;
import java.util.List;

/**
 * @author Peter Teixeira
 */
abstract class AbstractViewTab<T> extends Tab {
  private final Text errorText = new Text();
  private TableView<T> tableView;
  private List<String> sportSelections = Collections.emptyList();

  AbstractViewTab() {
    this.setClosable(false);
    this.setContent(this.createContent());
  }

  private Node createContent() {
    BorderPane pane = new BorderPane();
    pane.setCenter(this.createCenter());
    pane.setLeft(this.createLeftBar());
    return pane;
  }

  abstract Node createCenter();

  private Node createLeftBar() {
    VBox sidebar = new VBox();
    sidebar.setAlignment(Pos.TOP_CENTER);

    ObservableList<String> sports = FXCollections.observableArrayList();
    sports.addAll(this.sportSelections);
    ComboBox<String> sportsSelector = new ComboBox<>();
    sportsSelector.setItems(sports);

    Text errorText = new Text();
    errorText.setText("Error message goes here.");

    sidebar.getChildren().addAll(
        sportsSelector,
        errorText
    );

    return sidebar;
  }

  final void setErrorText(String errorMessage) {
    this.errorText.setText(errorMessage);
  }

  final void clearErrorText() {
    this.errorText.setText("");
  }

  final void populateTable(List<T> contents) {
    this.tableView.setItems(FXCollections.observableList(contents));
  }

  final void setSportSelections(List<String> sports) {
    this.sportSelections = sports;
  }


}

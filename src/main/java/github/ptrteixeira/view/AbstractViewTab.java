package github.ptrteixeira.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

/**
 * @author Peter Teixeira
 */
abstract class AbstractViewTab<T> extends Tab {
  private final Text errorText = new Text("");
  private final ObservableList<String> sportSelections =
      FXCollections.observableArrayList();
  private final ComboBox<String> sportsSelector =
      new ComboBox<>(sportSelections);

  AbstractViewTab() {
    this.setClosable(false);
    this.setContent(this.createContent());

    this.errorText.setFill(Color.RED);
    this.errorText.getStyleClass().add("error");

    this.sportsSelector.getStyleClass().add("sportSelector");
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

    sidebar.getChildren().addAll(
        this.sportsSelector,
        this.errorText
    );

    return sidebar;
  }

  final void setErrorText(String errorMessage) {
    this.errorText.setText(errorMessage);
  }

  final void clearErrorText() {
    this.errorText.setText("");
  }

  abstract void populateTable(List<T> contents);

  final void setSportSelections(List<String> sports) {
    this.sportSelections.addAll(sports);
    this.sportsSelector.getSelectionModel().select(0);
  }

  final void registerSportSelections(ChangeListener<String> selectionChangeListener) {
    this.sportsSelector.valueProperty().addListener(selectionChangeListener);
  }

  final void setSelectedSport(String sport) {
    System.out.println(String.format("Set selected sport to %s", sport));
    this.sportsSelector.getSelectionModel().select(sport);
  }
}

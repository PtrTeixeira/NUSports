package github.ptrteixeira.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Primary interface by which the presenter updates and responds
 * to events on the view.
 *
 * @author Peter Teixeira
 */
public interface ViewPresenter {
  void registerTabSwitchCallback(ChangeListener<Tab> tabChangeListener);
  void registerReloadCallback(EventHandler<MouseEvent> reloadCallback);
  void registerSportSelectionCallback(ChangeListener<String> selectionChangeListener);

  void setSelectableSports(List<String> sports);

  <T> void setTableContents(ObservableList<T> tableContents);

  DisplayType getCurrentDisplayType();
  void setCurrentDisplayType(DisplayType displayType);

  void clearErrorText();
  void setErrorText(String text);
}

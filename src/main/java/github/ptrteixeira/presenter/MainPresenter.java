package github.ptrteixeira.presenter;

import github.ptrteixeira.model.ConnectionFailureException;
import github.ptrteixeira.model.WebScraper;
import github.ptrteixeira.view.DisplayType;
import github.ptrteixeira.view.ViewPresenter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;

public class MainPresenter {
  private final WebScraper scraper;
  private final ViewPresenter presenter;
  private String currentDisplayItem;

  private MainPresenter(WebScraper scraper, ViewPresenter presenter) {
    this.scraper = scraper;
    this.presenter = presenter;

    this.registerCallbacks(this.presenter);
  }

  private void registerCallbacks(ViewPresenter presenter) {
    presenter.registerReloadCallback(this::reloadCallback);
    presenter.registerSportSelectionCallback(this::selectionChangeListener);
    presenter.registerTabSwitchCallback(this::tabChangeListener);
  }

  private void tabChangeListener(
      ObservableValue<? extends Tab> observableValue, Tab oldValue, Tab newValue) {
    if (presenter.getCurrentDisplayType().equals(DisplayType.SCHEDULE)) {
      presenter.setCurrentDisplayType(DisplayType.STANDINGS);
    } else {
      presenter.setCurrentDisplayType(DisplayType.SCHEDULE);
    }

    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem);
  }

  private void reloadCallback(MouseEvent mouseEvent) {
    this.scraper.clearCache(this.currentDisplayItem);

    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem);
  }

  private void selectionChangeListener(
      ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
    this.currentDisplayItem = newValue;

    this.changeSelection(this.presenter, this.scraper, newValue);
  }

  private void changeSelection(
      ViewPresenter presenter, WebScraper scraper, String currentSelection) {
    try {
      if (presenter.getCurrentDisplayType().equals(DisplayType.SCHEDULE)) {
        presenter.setTableContents(scraper.getSchedule(currentSelection));
      } else {
        presenter.setTableContents(scraper.getStandings(currentSelection));
      }
    } catch (ConnectionFailureException cfx) {
      presenter.setErrorText(cfx.getMessage());
      presenter.setTableContents(FXCollections.emptyObservableList());
    }
  }
}

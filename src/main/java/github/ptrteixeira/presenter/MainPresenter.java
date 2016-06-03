package github.ptrteixeira.presenter;

import github.ptrteixeira.model.ConnectionFailureException;
import github.ptrteixeira.model.WebScraper;
import github.ptrteixeira.view.DisplayType;
import github.ptrteixeira.view.ViewPresenter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;

import java.util.Collections;

public class MainPresenter {
  private final WebScraper scraper;
  private final ViewPresenter presenter;
  private String currentDisplayItem;

  // TODO split this up. A
  public MainPresenter(WebScraper scraper, ViewPresenter presenter) {
    this.scraper = scraper;
    this.presenter = presenter;

    this.currentDisplayItem = "";
  }

  public void loadPresenter() {
    this.registerCallbacks(this.presenter);
    this.setViewSportSelection(this.scraper, this.presenter);
  }

  private void registerCallbacks(ViewPresenter presenter) {
    presenter.registerReloadCallback(this::reloadCallback);
    presenter.registerSportSelectionCallback(this::selectionChangeListener);
    presenter.registerTabSwitchCallback(this::tabChangeListener);
  }

  private void setViewSportSelection(WebScraper webScraper, ViewPresenter presenter) {
    // TODO implement
    presenter.setSelectableSports(Collections.singletonList("Men's Basketball"));
  }

  private void tabChangeListener(
      ObservableValue<? extends Tab> observableValue, Tab oldValue, Tab newValue) {
    System.out.println("Tab changed.");
    if (presenter.getCurrentDisplayType().equals(DisplayType.SCHEDULE)) {
      presenter.setCurrentDisplayType(DisplayType.STANDINGS);
    } else {
      presenter.setCurrentDisplayType(DisplayType.SCHEDULE);
    }

    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem);
  }

  private void reloadCallback(MouseEvent mouseEvent) {
    System.out.println("Reload clicked.");
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
        presenter.setScheduleContents(scraper.getSchedule(currentSelection));
      } else {
        presenter.setStandingsContents(scraper.getStandings(currentSelection));
      }
    } catch (ConnectionFailureException cfx) {
      presenter.setErrorText(cfx.getMessage());
      presenter.setScheduleContents(FXCollections.emptyObservableList());
      presenter.setStandingsContents(FXCollections.emptyObservableList());
    }
  }
}

package github.ptrteixeira.presenter;

import github.ptrteixeira.model.ConnectionFailureException;
import github.ptrteixeira.model.WebScraper;
import github.ptrteixeira.view.DisplayType;
import github.ptrteixeira.view.ViewPresenter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class MainPresenter {
  private static final Logger logger = LogManager.getLogger();

  private final WebScraper scraper;
  private final ViewPresenter presenter;
  private String currentDisplayItem;

  public MainPresenter(WebScraper scraper, ViewPresenter presenter) {
    this.scraper = scraper;
    this.presenter = presenter;

    this.currentDisplayItem = "";
    logger.debug("Creating new MainPresenter");
  }

  public void loadPresenter() {
    logger.debug("Registering presenter on View.");
    this.registerCallbacks(this.presenter);
    this.setViewSportSelection(this.scraper, this.presenter);
    logger.trace("Successfully registered presenter on View.");
  }

  private void registerCallbacks(ViewPresenter presenter) {
    presenter.registerReloadCallback(this::reloadCallback);
    presenter.registerSportSelectionCallback(this::selectionChangeListener);
    presenter.registerTabSwitchCallback(this::tabChangeListener);
  }

  private void setViewSportSelection(WebScraper webScraper, ViewPresenter presenter) {
    // TODO implement
    presenter.setSelectableSports(Collections.singletonList("Men's Basketball"));
    logger.trace("Setting selectable sports to {}", "[Men's Basketball]");
  }

  private void tabChangeListener(
      ObservableValue<? extends Tab> observableValue, Tab oldValue, Tab newValue) {

    if (presenter.getCurrentDisplayType().equals(DisplayType.SCHEDULE)) {
      presenter.setCurrentDisplayType(DisplayType.STANDINGS);
      logger.debug("Tab changed from Schedule to Standings");
    } else {
      presenter.setCurrentDisplayType(DisplayType.SCHEDULE);
      logger.debug("Tab changed from Standings to Schedule");
    }

    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem);
  }

  private void reloadCallback(MouseEvent mouseEvent) {
    logger.debug("Reload clicked");
    this.scraper.clearCache(this.currentDisplayItem);

    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem);
  }

  private void selectionChangeListener(
      ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
    logger.debug("Selected sport changed from {} to {}", oldValue, newValue);
    this.currentDisplayItem = newValue;

    this.changeSelection(this.presenter, this.scraper, newValue);
  }

  private void changeSelection(
      ViewPresenter presenter, WebScraper scraper, String currentSelection) {
    try {
      if (presenter.getCurrentDisplayType().equals(DisplayType.SCHEDULE)) {
        logger.trace("Setting contents of Schedule table");
        presenter.setScheduleContents(scraper.getSchedule(currentSelection));
      } else {
        logger.trace("Setting contents of Standing table");
        presenter.setStandingsContents(scraper.getStandings(currentSelection));
      }
    } catch (ConnectionFailureException cfx) {
      logger.warn("Failed to connect", cfx);
      presenter.setErrorText(cfx.getMessage());
      presenter.setScheduleContents(FXCollections.emptyObservableList());
      presenter.setStandingsContents(FXCollections.emptyObservableList());
    }
  }
}

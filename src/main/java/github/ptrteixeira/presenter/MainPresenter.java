package github.ptrteixeira.presenter;

import github.ptrteixeira.model.ConnectionFailureException;
import github.ptrteixeira.model.Match;
import github.ptrteixeira.model.Standing;
import github.ptrteixeira.model.WebScraper;
import github.ptrteixeira.view.DisplayType;
import github.ptrteixeira.view.ViewPresenter;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class MainPresenter {
  private static final Logger logger = LogManager.getLogger();

  private final WebScraper scraper;
  private final ViewPresenter presenter;

  private String currentDisplayItem;

  public MainPresenter(WebScraper scraper, ViewPresenter presenter) {
    Objects.requireNonNull(scraper);
    Objects.requireNonNull(presenter);

    this.scraper = scraper;
    this.presenter = presenter;

    this.currentDisplayItem = "";
  }

  public void loadPresenter() {
    logger.debug("Registering presenter on View.");
    this.registerCallbacks(this.presenter);
    this.setViewSportSelection(this.scraper, this.presenter);
    logger.trace("Successfully registered presenter on View.");
    this.changeSelection(presenter, scraper, this.currentDisplayItem);
  }

  private void registerCallbacks(ViewPresenter presenter) {
    presenter.registerReloadCallback(this::reloadCallback);
    presenter.registerSportSelectionCallback(this::selectionChangeListener);
    presenter.registerTabSwitchCallback(this::tabChangeListener);
  }

  private void setViewSportSelection(WebScraper webScraper, ViewPresenter presenter) {
    List<String> selectableSports = webScraper.getSelectableSports();
    presenter.setSelectableSports(selectableSports);
    logger.trace("Setting selectable sports to {}", selectableSports);

    logger.trace("Setting currently selected sport to {}", selectableSports.get(0));
    this.currentDisplayItem = selectableSports.get(0);
  }

  private void tabChangeListener(ObservableValue<? extends Tab> observableValue,
                                 Tab oldValue, Tab newValue) {
    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem);
  }

  private void reloadCallback(MouseEvent mouseEvent) {
    logger.debug("Reload clicked");
    this.scraper.clearCache(this.currentDisplayItem);

    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem, true);
  }

  private void selectionChangeListener(ObservableValue<? extends String> observableValue,
                                       String oldValue, String newValue) {
    logger.debug("Selected sport changed from {} to {}", oldValue, newValue);
    this.currentDisplayItem = newValue;

    this.changeSelection(this.presenter, this.scraper, newValue);
  }

  private void changeSelection(ViewPresenter presenter, WebScraper scraper,
                               String currentSelection) {
    this.changeSelection(presenter, scraper, currentSelection, false);
  }

  private void changeSelection(ViewPresenter presenter, WebScraper scraper,
                               String currentSelection, boolean isRefresh) {
    try {
      presenter.clearErrorText();

      if (presenter.currentDisplayType() == null) {
        presenter.setCurrentDisplayType(DisplayType.SCHEDULE);
      }

      if (presenter.currentDisplayType().equals(DisplayType.SCHEDULE)) {
        List<Match> schedule = scraper.getSchedule(currentSelection);
        logger.trace("Setting contents of Schedule table to {}", schedule);
        presenter.setScheduleContents(schedule);
      } else {
        List<Standing> standings = scraper.getStandings(currentSelection);
        logger.trace("Setting contents of Standing table to {}", standings);
        presenter.setStandingsContents(standings);
      }
    } catch (ConnectionFailureException cfx) {
      logger.warn("Failed to connect", cfx);
      presenter.setErrorText(cfx.getMessage());
      if (!isRefresh) {
        presenter.setScheduleContents(Collections.emptyList());
        presenter.setStandingsContents(Collections.emptyList());
      }
    }
  }
}

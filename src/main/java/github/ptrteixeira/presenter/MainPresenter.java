package github.ptrteixeira.presenter;

import github.ptrteixeira.model.Match;
import github.ptrteixeira.model.Standing;
import github.ptrteixeira.model.WebScraper;
import github.ptrteixeira.view.DisplayType;
import github.ptrteixeira.view.ViewPresenter;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public final class MainPresenter {
  private static final Logger logger = LogManager.getLogger();

  private final WebScraper scraper;
  private final ViewPresenter presenter;
  private final Executor executor;

  private String currentDisplayItem;

  public MainPresenter(WebScraper scraper, ViewPresenter presenter, Executor executor) {
    Objects.requireNonNull(scraper);
    Objects.requireNonNull(presenter);
    Objects.requireNonNull(executor);

    this.scraper = scraper;
    this.presenter = presenter;
    this.executor = executor;

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

    this.changeSelection(this.presenter, this.scraper, this.currentDisplayItem, false);
  }

  private void selectionChangeListener(ObservableValue<? extends String> observableValue,
                                       String oldValue, String newValue) {
    logger.debug("Selected sport changed from {} to {}", oldValue, newValue);
    this.currentDisplayItem = newValue;

    this.changeSelection(this.presenter, this.scraper, newValue);
  }

  private void changeSelection(ViewPresenter presenter, WebScraper scraper,
                               String currentSelection) {
    this.changeSelection(presenter, scraper, currentSelection, true);
  }

  private void changeSelection(ViewPresenter presenter, WebScraper scraper,
                               String currentSelection, boolean shouldClear) {

    presenter.clearErrorText();

    EventHandler<WorkerStateEvent> onError = workerStateEvent -> {
      Throwable exn = workerStateEvent.getSource().getException();
      logger.warn("Failed to connect", exn);
      presenter.setErrorText("Failed to load data");

      if (shouldClear) {
        presenter.setScheduleContents(Collections.emptyList());
        presenter.setStandingsContents(Collections.emptyList());
      }
    };


    if (presenter.currentDisplayType() == DisplayType.SCHEDULE) {
      Task<List<Match>> task = new Task<List<Match>>() {
        @Override
        protected List<Match> call() throws Exception {
          return scraper.getSchedule(currentSelection);
        }
      };

      task.setOnSucceeded(workerStateEvent ->
          presenter.setScheduleContents(task.getValue()));
      task.setOnFailed(onError);
      executor.execute(task);
    } else if (presenter.currentDisplayType() == DisplayType.STANDINGS) {
      Task<List<Standing>> task = new Task<List<Standing>>() {
        @Override
        protected List<Standing> call() throws Exception {
          return scraper.getStandings(currentSelection);
        }
      };

      task.setOnSucceeded(workerStateEvent ->
          presenter.setStandingsContents(task.getValue()));
      task.setOnFailed(onError);
      executor.execute(task);
    }
  }
}

package com.github.ptrteixeira.nusports.view;

import com.github.ptrteixeira.nusports.model.Match;
import com.github.ptrteixeira.nusports.model.Standing;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MainView implements ViewPresenter {
  private static final Logger logger = LogManager.getLogger();

  private final ScheduleTab scheduleTab;
  private final StandingsTab standingsTab;
  private final TabPane tabPane;
  private final Button reloadButton;

  private DisplayType displayType;

  public MainView() {
    this(new ScheduleTab(), new StandingsTab());
  }

  MainView(ScheduleTab scheduleTab, StandingsTab standingsTab) {
    this.scheduleTab = scheduleTab;
    this.scheduleTab.setId("standingsTab");

    this.standingsTab = standingsTab;
    this.standingsTab.setId("scheduleTab");

    this.displayType = DisplayType.SCHEDULE;

    this.tabPane = new TabPane();
    this.tabPane.setId("tabPane");

    this.reloadButton = new Button("Reload");
    reloadButton.setId("reload");
  }

  @Override
  public void registerTabSwitchCallback(ChangeListener<Tab> tabChangeListener) {
    this.tabPane.getSelectionModel()
        .selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          // This part has to do with how the UI changes in response to a changed tab
          if (this.displayType == DisplayType.STANDINGS) {
            this.displayType = DisplayType.SCHEDULE;
          } else {
            this.displayType = DisplayType.STANDINGS;
          }

          // This part has to do with how the Model changes in response to a changed tab
          tabChangeListener.changed(observable, oldValue, newValue);
        });
  }

  @Override
  public void registerReloadCallback(EventHandler<MouseEvent> reloadCallback) {
    this.reloadButton.setOnMouseClicked(reloadCallback);
  }

  @Override
  public void registerSportSelectionCallback(ChangeListener<String> selectionChangeListener) {
    ChangeListener<String> callback = (observable, oldValue, newValue) -> {
      this.scheduleTab.setSelectedSport(newValue);
      this.standingsTab.setSelectedSport(newValue);

      selectionChangeListener.changed(observable, oldValue, newValue);
    };

    this.scheduleTab.registerSportSelections(callback);
    this.standingsTab.registerSportSelections(callback);
  }

  @Override
  public void setSelectableSports(List<String> sports) {
    logger.info("Set list of selectable sports to {}", sports);
    this.scheduleTab.setSportSelections(sports);
    this.standingsTab.setSportSelections(sports);

  }

  @Override
  public void setScheduleContents(List<Match> scheduleContents) {
    logger.trace("Set contents of schedule table to {}", scheduleContents);
    this.scheduleTab.populateTable(scheduleContents);
  }

  @Override
  public void setStandingsContents(List<Standing> standingsContents) {
    logger.trace("Reset contents of standings tab to {}", standingsContents);
    this.standingsTab.populateTable(standingsContents);
  }

  @Override
  public DisplayType currentDisplayType() {
    return this.displayType;
  }

  @Override
  public void setCurrentDisplayType(DisplayType displayType) {
    logger.debug("Set current DisplayType to {}", displayType.toString());
    this.displayType = displayType;
  }

  @Override
  public void clearErrorText() {
    logger.trace("Cleared the error message in the UI");
    this.standingsTab.clearErrorText();
    this.scheduleTab.clearErrorText();
  }

  @Override
  public void setErrorText(String text) {
    logger.trace("Set error message on UI to \"{}\"", text);
    this.scheduleTab.setErrorText(text);
    this.standingsTab.setErrorText(text);
  }

  @Override
  public Pane createView() {
    BorderPane pane = new BorderPane();
    pane.setCenter(this.createCenter());
    pane.setBottom(this.createBottomBar());

    pane.setMinWidth(600);
    pane.setMinHeight(600);

    logger.info("Creating main view");
    return pane;
  }

  private Node createBottomBar() {
    HBox bottomBar = new HBox();
    bottomBar.setAlignment(Pos.CENTER_RIGHT);
    Button reload = this.reloadButton;

    bottomBar.getChildren().add(reload);

    logger.trace("Creating bottom bar for main view");
    return bottomBar;
  }

  private Node createCenter() {
    TabPane tabPane = this.tabPane;
    tabPane.getTabs()
        .addAll(this.scheduleTab, this.standingsTab);

    logger.trace("Creating center for main view");
    return tabPane;
  }
}
package github.ptrteixeira.presenter;

import github.ptrteixeira.model.Match;
import github.ptrteixeira.model.Standing;
import github.ptrteixeira.view.DisplayType;
import github.ptrteixeira.view.ViewPresenter;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * @author Peter Teixeira
 */
final class MockViewPresenter implements ViewPresenter {
  ChangeListener<Tab> tabChangeListener;
  EventHandler<MouseEvent> reloadCallback;
  ChangeListener<String> selectionChange;
  List<String> selectableSports;
  List<Match> scheduleContents;
  List<Standing> standingsContents;
  DisplayType currentDisplayType;
  String errorText;


  @Override
  public void registerTabSwitchCallback(ChangeListener<Tab> tabChangeListener) {
    this.tabChangeListener = tabChangeListener;
  }

  @Override
  public void registerReloadCallback(EventHandler<MouseEvent> reloadCallback) {
    this.reloadCallback = reloadCallback;
  }

  @Override
  public void registerSportSelectionCallback(ChangeListener<String> selectionChangeListener) {

    this.selectionChange = selectionChangeListener;
  }

  @Override
  public void setSelectableSports(List<String> sports) {

    this.selectableSports = sports;
  }

  @Override
  public void setScheduleContents(List<Match> scheduleContents) {

    this.scheduleContents = scheduleContents;
  }

  @Override
  public void setStandingsContents(List<Standing> standingsContents) {

    this.standingsContents = standingsContents;
  }

  @Override
  public DisplayType currentDisplayType() {
    return this.currentDisplayType;
  }

  @Override
  public void setCurrentDisplayType(DisplayType displayType) {
    this.currentDisplayType = displayType;
  }

  @Override
  public void clearErrorText() {
    this.errorText = "";
  }

  @Override
  public void setErrorText(String text) {
    this.errorText = text;
  }

  @Override
  public Pane createView() {
    return null;
  }

  void changeTab() {
    if (this.currentDisplayType == DisplayType.SCHEDULE) {
      this.currentDisplayType = DisplayType.STANDINGS;
    } else {
      this.currentDisplayType = DisplayType.SCHEDULE;
    }

    this.tabChangeListener.changed(null, null, null);
  }
}

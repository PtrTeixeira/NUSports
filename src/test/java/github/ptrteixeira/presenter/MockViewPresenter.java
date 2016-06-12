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
 * Created by pteixeira on 6/6/16.
 */
public class MockViewPresenter implements ViewPresenter {
  @Override
  public void registerTabSwitchCallback(ChangeListener<Tab> tabChangeListener) {

  }

  @Override
  public void registerReloadCallback(EventHandler<MouseEvent> reloadCallback) {

  }

  @Override
  public void registerSportSelectionCallback(ChangeListener<String> selectionChangeListener) {

  }

  @Override
  public void setSelectableSports(List<String> sports) {

  }

  @Override
  public void setScheduleContents(List<Match> scheduleContents) {

  }

  @Override
  public void setStandingsContents(List<Standing> standingsContents) {

  }

  @Override
  public DisplayType currentDisplayType() {
    return null;
  }

  @Override
  public void setCurrentDisplayType(DisplayType displayType) {

  }

  @Override
  public void clearErrorText() {

  }

  @Override
  public void setErrorText(String text) {

  }

  @Override
  public Pane createView() {
    return null;
  }
}

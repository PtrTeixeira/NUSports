package com.github.ptrteixeira.nusports.view;

import com.github.ptrteixeira.nusports.model.Match;
import com.github.ptrteixeira.nusports.model.Standing;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

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

  void setScheduleContents(List<Match> scheduleContents);

  void setStandingsContents(List<Standing> standingsContents);

  DisplayType currentDisplayType();

  void setCurrentDisplayType(DisplayType displayType);

  void clearErrorText();

  void setErrorText(String text);

  Pane createView();
}

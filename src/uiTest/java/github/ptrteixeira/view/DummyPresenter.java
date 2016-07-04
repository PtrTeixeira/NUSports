package github.ptrteixeira.view;

import github.ptrteixeira.model.WebScraper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pteixeira on 6/11/16.
 */
final class DummyPresenter {
  private final WebScraper scraper;
  private final ViewPresenter viewPresenter;

  private String selectedSport = "";
  private String selectedTab = "";
  private boolean reloadClicked = false;


  DummyPresenter(WebScraper scraper, ViewPresenter viewPresenter) {
    this.scraper = scraper;
    this.viewPresenter = viewPresenter;

    List<String> sports = new ArrayList<>();
    sports.add("Sport 1");
    sports.add("Sport 2");
    this.viewPresenter.setSelectableSports(sports);
  }


  public void registerCallbacks() {
    this.viewPresenter.registerTabSwitchCallback((observable, oldValue, newValue) ->
        this.selectedTab = newValue.getText());
    this.viewPresenter.registerReloadCallback(event ->
        this.reloadClicked = true);
    this.viewPresenter.registerSportSelectionCallback((observable, oldValue, newValue) ->
        this.selectedSport = newValue);
  }

  public String selectedSport() {
    return this.selectedSport;
  }

  public String selectedTab() {
    return this.selectedTab;
  }

  public boolean reloadClicked() {
    return this.reloadClicked;
  }

  public void resetMock() {
    this.selectedTab = "";
    this.selectedSport = "";
    this.reloadClicked = false;
  }
}

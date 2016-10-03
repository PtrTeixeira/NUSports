package com.github.ptrteixeira.nusports.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Peter Teixeira
 */
public final class MockWebScraper implements WebScraper {
  public final List<String> cacheClears = new ArrayList<>();
  public final List<String> standingsRequests = new ArrayList<>();
  public final List<String> scheduleRequests = new ArrayList<>();
  private boolean failNextRequest = false;

  public void failNextRequest() {
    failNextRequest = true;
  }

  @Override
  public ObservableList<Standing> getStandings(String sport) throws ConnectionFailureException {
    standingsRequests.add(0, sport);
    if (failNextRequest) {
      this.failNextRequest = false;
      throw new ConnectionFailureException("Request failed");
    }

    return FXCollections.singletonObservableList(
        new Standing("Team 1", "Conference 1", "0 - 0")
    );
  }

  @Override
  public ObservableList<Match> getSchedule(String sport) throws ConnectionFailureException {
    scheduleRequests.add(0, sport);
    if (failNextRequest) {
      this.failNextRequest = false;
      throw new ConnectionFailureException("Request failed");
    }

    return FXCollections.singletonObservableList(
        new Match("10-21-1995", "Team 2", "W 2 - 1")
    );
  }

  @Override
  public List<String> getSelectableSports() {
    List<String> sports = new ArrayList<>();
    Collections.addAll(sports, "Sport 1", "Sport 2");

    return sports;
  }

  @Override
  public void clearCache(String sport) {
    cacheClears.add(0, sport);
  }
}

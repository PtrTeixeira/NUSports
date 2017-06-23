/*
 * Copyright (c) 2017 Peter Teixeira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.ptrteixeira.nusports.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** @author Peter Teixeira */
public final class MockWebScraper implements WebScraper {
  public final List<String> cacheClears = new ArrayList<>();
  public final List<String> standingsRequests = new ArrayList<>();
  public final List<String> scheduleRequests = new ArrayList<>();
  private boolean failNextRequest = false;

  public void failNextRequest() {
    failNextRequest = true;
  }

  public void reset() {
    this.standingsRequests.clear();
    this.scheduleRequests.clear();
    this.cacheClears.clear();
    this.failNextRequest = false;
  }

  @Override
  public ObservableList<Standing> getStandings(String sport) throws ConnectionFailureException {
    standingsRequests.add(0, sport);
    if (failNextRequest) {
      this.failNextRequest = false;
      throw new ConnectionFailureException("Request failed");
    }

    return FXCollections.singletonObservableList(new Standing("Team 1", "Conference 1", "0 - 0"));
  }

  @Override
  public ObservableList<Match> getSchedule(String sport) throws ConnectionFailureException {
    scheduleRequests.add(0, sport);
    if (failNextRequest) {
      this.failNextRequest = false;
      throw new ConnectionFailureException("Request failed");
    }

    return FXCollections.singletonObservableList(new Match("10-21-1995", "Team 2", "W 2 - 1"));
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

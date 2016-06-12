package github.ptrteixeira.presenter;

import github.ptrteixeira.model.ConnectionFailureException;
import github.ptrteixeira.model.Match;
import github.ptrteixeira.model.Standing;
import github.ptrteixeira.model.WebScraper;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by pteixeira on 6/6/16.
 */
public class MockWebScraper implements WebScraper {
  @Override
  public ObservableList<Standing> getStandings(String sport) throws ConnectionFailureException {
    return null;
  }

  @Override
  public ObservableList<Match> getSchedule(String sport) throws ConnectionFailureException {
    return null;
  }

  @Override
  public List<String> getSelectableSports() {
    return null;
  }

  @Override
  public void clearCache(String sport) {

  }
}

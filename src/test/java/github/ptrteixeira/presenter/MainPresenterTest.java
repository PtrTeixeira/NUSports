package github.ptrteixeira.presenter;

import github.ptrteixeira.model.Match;
import github.ptrteixeira.model.MockWebScraper;
import github.ptrteixeira.view.DisplayType;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assume.assumeThat;


/**
 * @author Peter Teixeira
 */
public class MainPresenterTest {
  private MockViewPresenter mockViewPresenter;
  private MockWebScraper mockWebScraper;
  private MainPresenter mainPresenter;

  private static final MouseEvent PRIMARY_MOUSE_CLICK = new MouseEvent(
      MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
      false, false, false, false, false, false, false, false, false, false, null
  );

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    this.mockViewPresenter = new MockViewPresenter();
    this.mockWebScraper = new MockWebScraper();
    this.mainPresenter = new MainPresenter(mockWebScraper, mockViewPresenter);

    mainPresenter.loadPresenter();
  }

  @Test
  public void testCreateWithNullArgumentsThrowsException() {
    expectedException.expect(NullPointerException.class);
    new MainPresenter(null, null);
  }

  @Test
  public void testCreateWithNullArgumentThrowsException() {
    expectedException.expect(NullPointerException.class);
    new MainPresenter(new MockWebScraper(), null);
  }

  @Test
  public void testDefaultDisplayTypeIsSchedule() {
    assertThat(mockViewPresenter.currentDisplayType(), is(DisplayType.SCHEDULE));
  }

  @Test
  public void testPopulatesSelectableSportsOnLoad() {
    assertThat(mockViewPresenter.selectableSports, contains("Sport 1", "Sport 2"));
  }

  @Test
  public void testWillAttemptToPopulateTableWhenLoadPresenterCalled() {
    assertThat(mockWebScraper.scheduleRequests, hasSize(greaterThan(0)));
    assertThat(mockViewPresenter.scheduleContents, is(not(empty())));
  }

  @Test
  public void testReloadClearsCache() {
    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockWebScraper.cacheClears, is(notNullValue()));
    assertThat(mockWebScraper.cacheClears, is(not(empty())));
  }

  @Test
  public void testReloadMakesNewRequestToModel() {
    int scheduleRequestCountBefore = mockWebScraper.scheduleRequests.size();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockWebScraper.scheduleRequests.size(),
        is(equalTo(scheduleRequestCountBefore + 1)));
    assertThat(mockWebScraper.scheduleRequests.get(0),
        isIn(mockWebScraper.getSelectableSports()));
  }

  @Test
  public void testOnRequestFailErrorTextIsSet() {
    mockWebScraper.failNextRequest();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockViewPresenter.errorText, is(not(isEmptyString())));
  }

  @Test
  public void testOnFailedReloadContentsAreNotChanged() {
    List<Match> initialContents = mockViewPresenter.scheduleContents;

    mockWebScraper.failNextRequest();
    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockViewPresenter.scheduleContents, is(equalTo(initialContents)));
  }

  @Test
  public void testOnFailedChangeSelectedSportContentsAreEmptied() {
    mockWebScraper.failNextRequest();
    mockViewPresenter.selectionChange
        .changed(new SimpleStringProperty("Sport 2"), "Sport 1", "Sport 2");

    assertThat(mockViewPresenter.scheduleContents, is(empty()));
  }

  @Test
  public void testMakingSuccessfulRequestAfterFailedRequestClearsErrorText() {
    mockWebScraper.failNextRequest();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    assumeThat(mockViewPresenter.errorText, is(not(isEmptyString())));

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    assertThat(mockViewPresenter.errorText, isEmptyString());
  }

  @Test
  public void testChangingSelectedSportMakesNewRequestToModel() {
    int scheduleRequests = mockWebScraper.scheduleRequests.size();

    mockViewPresenter.selectionChange.changed(
        new SimpleStringProperty("Sport 2"), "Sport 1", "Sport 2");

    assertThat(mockWebScraper.scheduleRequests.size(),
        is(equalTo(scheduleRequests + 1)));
    assertThat(mockWebScraper.scheduleRequests.get(0), is(equalTo("Sport 2")));
  }

  @Test
  public void testReloadingStandingsWillMakeARequestToModel() {
    int standingsRequests = mockWebScraper.standingsRequests.size();

    mockViewPresenter.setCurrentDisplayType(DisplayType.STANDINGS);
    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockWebScraper.standingsRequests.size(),
        is(equalTo(standingsRequests + 1)));
    assertThat(mockWebScraper.standingsRequests.get(0), is("Sport 1"));
  }
}
package github.ptrteixeira.presenter;

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

import github.ptrteixeira.model.Match;
import github.ptrteixeira.model.MockWebScraper;
import github.ptrteixeira.view.DisplayType;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.concurrent.Executor;


/**
 * This class is liberally sprinkled with Thread.sleep statements and other threading nuisances.
 * Unfortunately, this is necessary to get the JavaFX threading system, which controls Tasks, linked
 * up to the thread that is running the tests. Oh well.
 *
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

  @BeforeClass
  public static void launchJavaFX() throws Exception {
    Thread t = new Thread(() -> Application.launch(AsNonApp.class));
    t.setDaemon(true);
    t.start();

    Thread.sleep(200);
  }

  @Before
  public void setUp() {
    this.mockViewPresenter = new MockViewPresenter();
    this.mockWebScraper = new MockWebScraper();
    Executor immediateExecutor = Runnable::run;
    this.mainPresenter = new MainPresenter(mockWebScraper, mockViewPresenter, immediateExecutor);

    mainPresenter.loadPresenter();
  }

  @Test
  public void testCreateWithNullArgumentsThrowsException() {
    expectedException.expect(NullPointerException.class);
    new MainPresenter(null, null, null);
  }

  @Test
  public void testCreateWithNullArgumentThrowsException() {
    expectedException.expect(NullPointerException.class);
    new MainPresenter(new MockWebScraper(), null, null);
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
  public void testWillAttemptToPopulateTableWhenLoadPresenterCalled() throws Exception {
//    Thread.sleep(4000);
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
  public void testOnRequestFailErrorTextIsSet() throws Exception {
    mockWebScraper.failNextRequest();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    Thread.sleep(200);
    assertThat(mockViewPresenter.errorText, is(not(isEmptyString())));
  }

  @Test
  public void testOnFailedReloadContentsAreNotChanged() throws Exception {
    Thread.sleep(200);
    List<Match> initialContents = mockViewPresenter.scheduleContents;

    mockWebScraper.failNextRequest();
    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    Thread.sleep(200);
    assertThat(mockViewPresenter.scheduleContents, is(equalTo(initialContents)));
  }

  @Test
  public void testOnFailedChangeSelectedSportContentsAreEmptied() throws Exception {
    mockWebScraper.failNextRequest();
    mockViewPresenter.selectionChange
        .changed(new SimpleStringProperty("Sport 2"), "Sport 1", "Sport 2");

    Thread.sleep(200);
    assertThat(mockViewPresenter.scheduleContents, is(empty()));
  }

  @Test
  public void testMakingSuccessfulRequestAfterFailedRequestClearsErrorText() throws Exception {
    mockWebScraper.failNextRequest();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    Thread.sleep(200);
    assumeThat(mockViewPresenter.errorText, is(not(isEmptyString())));

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    Thread.sleep(200);
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

  @Test
  public void testChangingTabsMakesRequestToModel() {
    assumeThat(mockViewPresenter.currentDisplayType, is(DisplayType.SCHEDULE));
    int scheduleRequests = mockWebScraper.scheduleRequests.size();
    int standingsRequests = mockWebScraper.standingsRequests.size();


    mockViewPresenter.changeTab();

    assertThat(mockViewPresenter.currentDisplayType, is(DisplayType.STANDINGS));
    assertThat(mockWebScraper.standingsRequests.size(),
        is(equalTo(standingsRequests + 1)));
    assertThat(mockWebScraper.standingsRequests.get(0), is("Sport 1"));


    mockViewPresenter.changeTab();

    assertThat(mockViewPresenter.currentDisplayType, is(DisplayType.SCHEDULE));
    assertThat(mockWebScraper.scheduleRequests.size(),
        is(equalTo(scheduleRequests + 1)));
    assertThat(mockWebScraper.scheduleRequests.get(0), is("Sport 1"));
  }

  public static class AsNonApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
      // no-op
      // Required to spawn a JavaFX thread, which allows
      // JavaFX thread handlers like Task to be created
    }
  }
}
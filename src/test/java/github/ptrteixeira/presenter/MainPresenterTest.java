package github.ptrteixeira.presenter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import github.ptrteixeira.model.Match;
import github.ptrteixeira.model.MockWebScraper;
import github.ptrteixeira.view.DisplayType;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

  private static final MouseEvent PRIMARY_MOUSE_CLICK = new MouseEvent(
      MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
      false, false, false, false, false, false, false, false, false, false, null
  );

  @BeforeAll
  public static void launchJavaFX() throws Exception {
    Thread t = new Thread(() -> Application.launch(AsNonApp.class));
    t.setDaemon(true);
    t.start();
  }

  @BeforeEach
  public void setUp() {
    this.mockViewPresenter = new MockViewPresenter();
    this.mockWebScraper = new MockWebScraper();
    Executor immediateExecutor = Runnable::run;
    MainPresenter mainPresenter = new MainPresenter(mockWebScraper, mockViewPresenter, immediateExecutor);

    mainPresenter.loadPresenter();
  }

  @BeforeEach
  public void pause() throws Exception {
    Thread.sleep(200);
  }

  @Test
  public void testCreateWithNullArgumentsThrowsException() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new MainPresenter(null, null, null));
  }

  @Test
  public void testCreateWithNullArgumentThrowsException() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new MainPresenter(new MockWebScraper(), null, null));
  }

  @Test
  public void testDefaultDisplayTypeIsSchedule() {
    assertThat(mockViewPresenter.currentDisplayType)
        .isEqualTo(DisplayType.SCHEDULE);
  }

  @Test
  public void testPopulatesSelectableSportsOnLoad() {
    assertThat(mockViewPresenter.selectableSports)
        .contains("Sport 1", "Sport 2");
  }

  @Test
  public void testWillAttemptToPopulateTableWhenLoadPresenterCalled() throws Exception {
    assertThat(mockViewPresenter.scheduleContents)
        .isNotEmpty();
    assertThat(mockWebScraper.scheduleRequests)
        .size().isGreaterThan(0);
  }

  @Test
  public void testReloadClearsCache() {
    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockWebScraper.cacheClears)
        .isNotNull()
        .isNotEmpty();
  }

  @Test
  public void testReloadMakesNewRequestToModel() {
    int scheduleRequestCountBefore = mockWebScraper.scheduleRequests.size();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockWebScraper.scheduleRequests)
        .size().isEqualTo(scheduleRequestCountBefore + 1)
        .returnToIterable()
        .first().isIn(mockWebScraper.getSelectableSports());
  }

  @Test
  public void testOnRequestFailErrorTextIsSet() throws Exception {
    mockWebScraper.failNextRequest();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    Thread.sleep(200);

    assertThat(mockViewPresenter.errorText)
        .isNotEmpty();
  }

  @Test
  public void testOnFailedReloadContentsAreNotChanged() throws Exception {
    List<Match> initialContents = mockViewPresenter.scheduleContents;

    mockWebScraper.failNextRequest();
    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    Thread.sleep(200);
    assertThat(mockViewPresenter.scheduleContents)
        .isEqualTo(initialContents);
  }

  @Test
  public void testOnFailedChangeSelectedSportContentsAreEmptied() throws Exception {
    mockWebScraper.failNextRequest();
    mockViewPresenter.selectionChange
        .changed(new SimpleStringProperty("Sport 2"), "Sport 1", "Sport 2");

    Thread.sleep(200);
    assertThat(mockViewPresenter.scheduleContents)
        .isEmpty();
  }

  @Test
  public void testMakingSuccessfulRequestAfterFailedRequestClearsErrorText() throws Exception {
    mockWebScraper.failNextRequest();

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    Thread.sleep(200);

    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);
    Thread.sleep(200);
    assertThat(mockViewPresenter.errorText)
        .isEmpty();
  }

  @Test
  public void testChangingSelectedSportMakesNewRequestToModel() {
    int scheduleRequests = mockWebScraper.scheduleRequests.size();

    mockViewPresenter.selectionChange.changed(
        new SimpleStringProperty("Sport 2"), "Sport 1", "Sport 2");

    assertThat(mockWebScraper.scheduleRequests)
        .size().isEqualTo(scheduleRequests + 1)
        .returnToIterable()
        .first().isEqualTo("Sport 2");

  }

  @Test
  public void testReloadingStandingsWillMakeARequestToModel() {
    int standingsRequests = mockWebScraper.standingsRequests.size();

    mockViewPresenter.setCurrentDisplayType(DisplayType.STANDINGS);
    mockViewPresenter.reloadCallback.handle(PRIMARY_MOUSE_CLICK);

    assertThat(mockWebScraper.standingsRequests)
        .size().isEqualTo(standingsRequests + 1)
        .returnToIterable()
        .first().isEqualTo("Sport 1");
  }

  @Test
  public void testChangingTabsMakesRequestToModel() throws Exception {
    int scheduleRequests = mockWebScraper.scheduleRequests.size();
    int standingsRequests = mockWebScraper.standingsRequests.size();


    mockViewPresenter.changeTab();

    assertThat(mockViewPresenter.currentDisplayType)
        .isEqualTo(DisplayType.STANDINGS);
    assertThat(mockWebScraper.standingsRequests)
        .size().isEqualTo(standingsRequests + 1)
        .returnToIterable()
        .first().isEqualTo("Sport 1");


    mockViewPresenter.changeTab();

    assertThat(mockViewPresenter.currentDisplayType)
        .isEqualTo(DisplayType.SCHEDULE);
    assertThat(mockWebScraper.scheduleRequests)
        .size().isEqualTo(scheduleRequests + 1)
        .returnToIterable()
        .first().isEqualTo("Sport 1");
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
package github.ptrteixeira.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasChildren;
import static org.testfx.matcher.base.NodeMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 * @author Peter Teixeira
 */
public class MainViewSpec extends ApplicationTest {
  private DummyPresenter presenter;
  private ViewPresenter view;

  @Override
  public void start(Stage stage) throws Exception {
    this.view = new MainView();
    this.presenter = new DummyPresenter(null, view);

    Scene scene = new Scene(view.createView());
    this.presenter.registerCallbacks();

    stage.setTitle("NU Sports");
    stage.setScene(scene);
    stage.show();
  }

  @After
  public void resetPresenter() {
    this.presenter.resetMock();
  }

  @Test
  public void testViewCreated() {
    verifyThat(".error", isVisible());
    verifyThat("#tabPane", isVisible());
    verifyThat("Reload", isVisible());
    verifyThat(".sportSelector", isVisible());
  }

  @Test
  public void testChangingTabsLoadsNewView() {
    assertThat(this.presenter.selectedTab(), is(""));

    clickOn("Standings");
    assertThat(this.presenter.selectedTab(), is("Standings"));
    assertThat(this.view.currentDisplayType(), is(DisplayType.STANDINGS));
    clickOn("Schedule");
    assertThat(this.presenter.selectedTab(), is("Schedule"));
    assertThat(this.view.currentDisplayType(), is(DisplayType.SCHEDULE));
  }

  @Test
  public void testReloadGetsNewContent() {
    assertThat(this.presenter.reloadClicked(), is(false));

    clickOn("Reload");

    assertThat(this.presenter.reloadClicked(), is(true));
  }


  @Test
  public void testCanChangeSportSelection() {
    assumeThat(this.presenter.selectedSport(), is(""));

    clickOn(".sportSelector");
    clickOn("Sport 2");

    assertThat(this.presenter.selectedSport(), is("Sport 2"));
  }

  @Test
  public void testCanModifyErrorMessage() {
    verifyThat(".error", hasText(""));

    this.view.setErrorText("Error Message");
    verifyThat(".error", hasText("Error Message"));

    this.view.clearErrorText();
    verifyThat(".error", hasText(""));
  }

  @Test
  public void testCanModifyTableContents() {
    this.view.setStandingsContents(Collections.emptyList());
    this.view.setScheduleContents(Collections.emptyList());

    verifyThat(".table-view", isVisible());
    verifyThat(".table-view", hasChildren(0, ".row"));
  }
}

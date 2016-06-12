package github.ptrteixeira.presenter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Peter Teixeira
 */
public class MainPresenterTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

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



}
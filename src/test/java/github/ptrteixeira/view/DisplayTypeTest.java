package github.ptrteixeira.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

import org.junit.jupiter.api.Test;

/**
 * The point of this class is mostly just for fun.
 *
 * <p>
 * Like, there very, very little point in testing the
 * {@link DisplayType#toString()} method, that's just silly.
 * On the flip side, I really like the {@code switch(this) {}}
 * construction, so I'll take it.
 * </p>
 */
public class DisplayTypeTest {
  @Test
  public void testToStringReturnsExpectedValues() {
    assertThat(DisplayType.SCHEDULE, hasToString("Schedule"));
    assertThat(DisplayType.STANDINGS, hasToString("Standings"));
  }
}

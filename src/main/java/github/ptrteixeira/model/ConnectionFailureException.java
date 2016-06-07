package github.ptrteixeira.model;

import java.io.IOException;

/**
 * Represents a failure of the {@link WebScraper} to connect to the
 * site to be scraped. The cause should be specified by the throwing
 * method, but will typically do to a failure to connect to the internet.
 * This is a checked exception; it is perfectly reasonable for this
 * exception to be thrown in the normal operation of the application. Also,
 * {@link IOException} is checked, so what can you do.
 *
 * @author Peter Teixeira
 */
public class ConnectionFailureException extends IOException {
  ConnectionFailureException(String message) {
    super(message);
  }
}

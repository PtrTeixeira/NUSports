package github.ptrteixeira.model;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * A supplier for {@link Document} objects, which are parsed
 * in the {@link WebScraper}. I am not convinced that I have the
 * right abstraction here. But it does permit unit testing, which is nice.
 *
 * @author Peter Teixeira
 */
interface DocumentSource {
  /**
   * Return the {@link Document} which can be accessed at the given URL.
   *
   * @param url URL of the web page to be accessed
   * @return {@code JSoup} interpretation of the accessed web-page
   * @throws IOException If the webpage cannot be accessed for whatever reason
   */
  Document get(String url) throws IOException;
}

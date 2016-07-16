package github.ptrteixeira.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Implementation of the access layer for foreign resources.
 * In particular, it is designed to get relevant documents
 * from the CAA website.
 *
 * <p>
 * I am really not sure how much specification
 * really needs to go in here; perhaps it would be better to just make
 * this {@code DocumentSourceImpl}.
 * </p>
 *
 * @author Peter Teixeira
 */
final class NUDocumentSource implements DocumentSource {
  private static final Logger logger = LogManager.getLogger();

  @Override
  public Document get(String url) throws IOException {
    logger.debug("Making query to {}", url);
    return Jsoup.connect(url)
        .header("Connection", "keep-alive")
        .header("Accept-Encoding", "gzip, deflate, sdch")
        .userAgent("Chrome/51")
        .maxBodySize(0)
        .timeout(7000)
        .get();
  }
}

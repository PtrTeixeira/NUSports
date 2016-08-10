package github.ptrteixeira.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Foreign source access layer
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

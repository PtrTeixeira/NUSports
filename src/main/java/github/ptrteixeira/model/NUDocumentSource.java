package github.ptrteixeira.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Foreign source access layer
 *
 * @author Peter Teixeira
 */
final class NUDocumentSource implements DocumentSource {

  @Override
  public Document get(String url) throws IOException {
    return Jsoup.connect(url)
        .header("Connection", "keep-alive")
        .header("Accept-Encoding", "gzip, deflate, sdch")
        .userAgent("Chrome/51")
        .maxBodySize(0)
        .timeout(7000)
        .get();
  }
}

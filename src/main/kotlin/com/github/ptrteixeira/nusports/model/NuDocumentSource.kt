/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

/**
 * Implementation of the access layer for foreign resources. In particular, it is designed to load
 * relevant documents from the CAA website.
 *
 * I am really not sure how much specification really needs to go in here; perhaps it would be
 * better to just make this `DocumentSourceImpl`.

 * @author Peter Teixeira
 */
internal class NuDocumentSource @Inject
constructor() : DocumentSource {

    // Actually blocking right now.
    override suspend fun load(url: String): Document {
        logger.debug("Making query to {}", url)
        return Jsoup.connect(url)
            .header("Connection", "keep-alive")
            .header("Accept-Encoding", "gzip, deflate, sdch")
            .userAgent("Chrome/51")
            .maxBodySize(0)
            .timeout(7000)
            .get()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}

/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

/**
 * Implementation of the access layer for foreign resources. In particular, it is designed to load
 * relevant documents from the CAA website.
 *
 * I am really not sure how much specification really needs to go in here; perhaps it would be
 * better to just make this `DocumentSourceImpl`.

 * @author Peter Teixeira
 */
internal class NuDocumentSource(private val client: OkHttpClient) : DocumentSource {

    override suspend fun load(url: String): Document {
        logger.debug("Making query to {}", url)
        val request = Request.Builder()
                .get().url(url)
                .header("User-Agent", "Chrome/70")
                .build()
        val responseBody = client.newCall(request).read().body()

        if (responseBody == null) {
            throw IOException("Body was empty")
        } else {
            val body = responseBody.string()
            return Jsoup.parse(body)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}

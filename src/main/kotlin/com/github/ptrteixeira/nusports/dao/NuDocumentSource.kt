/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of the access layer for foreign resources. In particular, it is designed to load
 * relevant documents from the CAA website.
 *
 * I am really not sure how much specification really needs to go in here; perhaps it would be
 * better to just make this `DocumentSourceImpl`.

 * @author Peter Teixeira
 */
internal class NuDocumentSource @Inject constructor(private val client: OkHttpClient) : DocumentSource {

    // Actually blocking right now.
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

    private suspend fun Call.read(): Response {
        return suspendCoroutine {
            this.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    it.resume(response)
                }
            })
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}

/*
 * Copyright (c) 2017 Peter Teixeira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.ptrteixeira.nusports.model

import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
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

    @Throws(IOException::class)
    override fun load(url: String): Document {
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

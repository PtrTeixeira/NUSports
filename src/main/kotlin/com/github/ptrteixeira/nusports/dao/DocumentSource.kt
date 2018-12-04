/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import org.jsoup.nodes.Document
import java.io.IOException

/**
 * A supplier for [Document] objects, which are parsed in the [WebScraper]. I am not
 * convinced that I have the right abstraction here. But it does permit unit testing, which is nice.

 * @author Peter Teixeira
 */
internal interface DocumentSource {
    /**
     * Return the [Document] which can be accessed at the given URL.

     * @param url URL of the web page to be accessed
     * @return `JSoup` interpretation of the accessed web-page
     * @throws IOException If the webpage cannot be accessed for whatever reason
     */
    suspend fun load(url: String): Document
}

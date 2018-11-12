/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

internal class LocalDocumentSource(private val pathInFolder: String) : DocumentSource {
    override suspend fun load(url: String): Document {
        return Jsoup.parse(File("src/test/resources/$pathInFolder.html"), "UTF8", ".")
    }
}

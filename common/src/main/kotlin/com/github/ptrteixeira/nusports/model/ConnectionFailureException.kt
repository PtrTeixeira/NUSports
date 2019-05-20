/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

import java.io.IOException

/**
 * Represents a failure of the [WebScraper] to connect to the site to be scraped. The cause
 * should be specified by the throwing method, but will typically do to a failure to connect to the
 * internet. This is a checked exception; it is perfectly reasonable for this exception to be thrown
 * in the normal operation of the application.
 *
 * @author Peter Teixeira
 */
class ConnectionFailureException(message: String) : IOException(message)

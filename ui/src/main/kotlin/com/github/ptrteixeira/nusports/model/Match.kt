/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

/**
 * Data object for sports matches
 *
 * @author Peter Teixeira
 *
 *
 */
data class Match(
    val date: String,
    val opponent: String,
    val result: String
)

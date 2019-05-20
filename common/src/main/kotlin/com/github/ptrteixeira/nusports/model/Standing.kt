/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

/**
 * Data object to record sports win/loss ratios
 *
 * In context, used to generate relative standings between teams based on their win/loss
 * records.
 *
 * @property teamName Name of the team whose standings this records. eg, "Northeastern"
 * @property conference Record within conference play, eg "5-2"
 * @property overall Overall record, including both in-conference and out-of conference play
 */
data class Standing(
    val teamName: String,
    val conference: String,
    val overall: String
)

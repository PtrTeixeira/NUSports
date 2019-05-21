/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.model

/**
 * Public API of WebScraper objects.

 * @author Peter
 * *
 * @version 0.1
 */
interface WebScraper {
    /**
     * Sports that are supported by this web scraper.
     *
     */
    val selectableSports: List<String>

    /**
     * Get the standings (ie, win/loss records) for the given sport.

     * @param sport String label of what sport to load
     * *
     * @return A list of the Standings for that sport
     * *
     * @throws ConnectionFailureException if the site this scraper is attached to cannot be reached
     */
    @Throws(ConnectionFailureException::class)
    suspend fun getStandings(sport: String): List<Standing>

    /**
     * Get the schedule for a specific team in the given sport.
     *
     *
     * For this particular project, the team (Northeastern) is dictated as part of the definition
     * of the web scraper. It may be better to extend this so that it takes the team name and the name
     * of the sport.

     * @param sport String label of what sport to load
     * *
     * @return A list of Matches for that sport
     * *
     * @throws ConnectionFailureException if the site this scraper is attached to cannot be reached
     */
    @Throws(ConnectionFailureException::class)
    suspend fun getSchedule(sport: String): List<Match>

    /**
     * Clear the cache for the given sport, typically to force an update.

     * @param sport String label of what sport to clear the cache of.
     */
    fun clearCache(sport: String)
}

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

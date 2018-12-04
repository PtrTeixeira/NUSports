/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import com.github.ptrteixeira.nusports.model.CalendarResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CaaService {
    @GET("services/responsive-calendar.ashx")
    fun getSchedule(
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("sport_id") sportId: String,
        @Query("school_id") schoolId: String
    ): Call<List<CalendarResponseItem>>
}
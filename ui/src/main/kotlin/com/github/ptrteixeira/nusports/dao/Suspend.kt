/* Released under the MIT license, 2018 */

package com.github.ptrteixeira.nusports.dao

import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun <T> retrofit2.Call<T>.read(): T? {
    return suspendCoroutine {
        this.enqueue(object : Callback<T> {
            override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
                it.resumeWithException(t)
            }

            override fun onResponse(call: retrofit2.Call<T>, response: Response<T>) {
                it.resume(response.body())
            }
        })
    }
}

internal suspend fun okhttp3.Call.read(): okhttp3.Response {
    return suspendCoroutine {
        this.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                it.resume(response)
            }
        })
    }
}
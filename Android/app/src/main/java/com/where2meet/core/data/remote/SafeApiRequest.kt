package com.where2meet.core.data.remote

import com.where2meet.utils.ApiException
import logcat.logcat
import org.json.JSONException
import retrofit2.Response

open class SafeApiRequest {
    suspend fun <T : Any> apiRequest(
        call: suspend () -> Response<T>,
        decodeErrorJson: suspend (String) -> String,
    ): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            logcat { "Response is " + response.body().toString() }
            return response.body()!!
        } else {
            val error = response.errorBody()?.string()
            val message = StringBuilder()
            error?.let {
                try {
                    message.append(decodeErrorJson(it))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            throw ApiException(message.toString())
        }
    }
}

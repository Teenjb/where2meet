package com.where2meet.core.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.where2meet.utils.NoInternetException
import logcat.asLog
import logcat.logcat
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class NoConnectionInterceptor(
    private val context: Context,
) : Interceptor {
    companion object {
        private const val TIMEOUT_DURATION = 1500
        private const val DEFAULT_PORT = 53
    }

    override fun intercept(chain: Chain): Response {
        if (!isConnectionOn() || !isInternetAvailable()) {
            throw NoInternetException()
        }
        return chain.proceed(chain.request())
    }

    private fun isConnectionOn(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager

        val network = connectivityManager.activeNetwork
        val connection =
            connectivityManager.getNetworkCapabilities(network)

        return connection != null && (
            connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            )
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = TIMEOUT_DURATION
            val sock = Socket()
            val sockAddress = InetSocketAddress("8.8.8.8", DEFAULT_PORT)

            sock.connect(sockAddress, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            logcat { e.asLog() }
            false
        }
    }
}

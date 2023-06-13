package com.where2meet.utils

import okio.IOException

class ApiException(message: String) : IOException(message)
class NoInternetException : IOException("You're not connected to the internet")

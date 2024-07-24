package com.example.stockmarketcheck.core.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)

    class Error<T>(message: String?, data: T?) : Resource<T>(data, message)

    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
}

// https://www.notion.so/StockMarket-app-fd555472e30c45ef8586565dc35d7d42?pvs=4#6139797a  e6674b39901a54c2ceeaec83
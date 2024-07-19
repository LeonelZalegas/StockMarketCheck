package com.example.stockmarketcheck.mainFeature.data.remote

import com.example.stockmarketcheck.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockClient {
    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(
        @Query("apikey") apikey: String = BuildConfig.API_KEY,
    ): ResponseBody
}
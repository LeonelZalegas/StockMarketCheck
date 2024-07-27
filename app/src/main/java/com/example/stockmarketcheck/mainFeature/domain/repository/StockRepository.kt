package com.example.stockmarketcheck.mainFeature.domain.repository

import com.example.stockmarketcheck.core.util.Resource
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyInfo
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo>
}
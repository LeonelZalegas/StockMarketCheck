package com.example.stockmarketcheck.mainFeature.domain.repository

import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.example.stockmarketcheck.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<CompanyListing>>>
}
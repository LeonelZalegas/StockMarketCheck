package com.example.stockmarketcheck.mainFeature.presentation.company_listings

import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing

data class CompanyListingsState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
)

package com.example.stockmarketcheck.mainFeature.presentation.company_listings

sealed class CompanyListingsEvent {
    object Refresh : CompanyListingsEvent()

    data class OnSearchQueryChange(val query: String) : CompanyListingsEvent()
}
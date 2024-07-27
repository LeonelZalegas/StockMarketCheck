package com.example.stockmarketcheck.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object CompanyListings

@Serializable
data class CompanyInfo(val symbol: String)
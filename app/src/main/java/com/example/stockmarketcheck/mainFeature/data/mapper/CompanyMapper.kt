package com.example.stockmarketcheck.mainFeature.data.mapper

import com.example.stockmarketcheck.mainFeature.Domain.model.CompanyListing
import com.example.stockmarketcheck.mainFeature.data.local.CompanyListingEntity

fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange,
    )
}

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange,
    )
}
package com.example.stockmarketcheck.mainFeature.data.mapper

import com.example.stockmarketcheck.mainFeature.data.local.CompanyListingEntity
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing

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
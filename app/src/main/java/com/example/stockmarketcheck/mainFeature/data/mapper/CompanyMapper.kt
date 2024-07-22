package com.example.stockmarketcheck.mainFeature.data.mapper

import com.example.stockmarketcheck.mainFeature.data.local.CompanyListingEntity
import com.example.stockmarketcheck.mainFeature.data.remote.dto.CompanyInfoDto
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyInfo
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

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    // ponemos ?: "" xq puede ser q te pases de llamadas gratuitas y la API te devuelva
    // otro JSON q no corresponde con los fields de CompanyInfoDto (no existen) x ende
    // mejor que devuelva "" a q crashee
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: "",
    )
}
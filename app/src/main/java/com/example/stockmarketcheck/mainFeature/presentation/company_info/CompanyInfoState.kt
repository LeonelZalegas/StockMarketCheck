package com.example.stockmarketcheck.mainFeature.presentation.company_info

import com.example.stockmarketcheck.mainFeature.domain.model.CompanyInfo
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockIntradayInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

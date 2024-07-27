package com.example.stockmarketcheck.mainFeature.domain.model

import java.time.LocalDateTime

data class IntradayInfo(
    val date: LocalDateTime,
    val close: Double,
)

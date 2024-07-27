package com.example.stockmarketcheck.mainFeature.data.remote.dto

import com.google.gson.annotations.SerializedName

// Seria el "Response" que llama a aristidevs y es el que sera rellenado x ConverterFactoryJson
data class CompanyInfoDto(
    @SerializedName("Symbol") val symbol: String?,
    @SerializedName("Description") val description: String?,
    @SerializedName("Name") val name: String?,
    @SerializedName("Country") val country: String?,
    @SerializedName("Industry") val industry: String?,
)

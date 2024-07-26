package com.example.stockmarketcheck.mainFeature.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketcheck.core.util.Resource
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel
    @Inject
    constructor(
        // https://www.notion.so/StockMarket-app-fd555472e30c45ef8586565dc35d7d42?pvs=4#944cfb21f2114efdbf2842429e3a6e76
        private val savedStateHandle: SavedStateHandle,
        private val repository: StockRepository,
    ) : ViewModel() {
        var state by mutableStateOf(CompanyInfoState())

        init {
            viewModelScope.launch {
                val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
                state = state.copy(isLoading = true)
                val companyInfoResult = async { repository.getCompanyInfo(symbol) }
                val intradayInfoResult = async { repository.getIntradayInfo(symbol) }

                val companyInfo = companyInfoResult.await()
                val intradayInfo = intradayInfoResult.await()

                state =
                    when {
                        companyInfo is Resource.Error ->
                            state.copy(
                                isLoading = false,
                                error = companyInfo.message,
                                company = null,
                            )
                        intradayInfo is Resource.Error ->
                            state.copy(
                                isLoading = false,
                                error = intradayInfo.message,
                                company = (companyInfo as? Resource.Success)?.data,
                            )
                        companyInfo is Resource.Success && intradayInfo is Resource.Success ->
                            state.copy(
                                isLoading = false,
                                company = companyInfo.data,
                                stockIntradayInfos = intradayInfo.data ?: emptyList(),
                                error = null,
                            )
                        else -> state.copy(isLoading = false, error = "An unexpected error occurred")
                    }
            }
        }
    }
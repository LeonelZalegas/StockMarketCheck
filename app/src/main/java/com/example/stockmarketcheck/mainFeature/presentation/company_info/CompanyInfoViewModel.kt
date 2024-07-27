package com.example.stockmarketcheck.mainFeature.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketcheck.core.util.Resource
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val repository: StockRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(CompanyInfoState())
        val state: StateFlow<CompanyInfoState> = _state.asStateFlow()

        init {
            viewModelScope.launch {
                val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
                _state.update { it.copy(isLoading = true) }
                val companyInfoResult = async { repository.getCompanyInfo(symbol) }
                val intradayInfoResult = async { repository.getIntradayInfo(symbol) }

                val companyInfo = companyInfoResult.await()
                val intradayInfo = intradayInfoResult.await()

                _state.update {
                    when {
                        companyInfo is Resource.Error ->
                            it.copy(
                                isLoading = false,
                                error = companyInfo.message,
                                company = null,
                            )
                        intradayInfo is Resource.Error ->
                            it.copy(
                                isLoading = false,
                                error = intradayInfo.message,
                                company = (companyInfo as? Resource.Success)?.data,
                            )
                        companyInfo is Resource.Success && intradayInfo is Resource.Success ->
                            it.copy(
                                isLoading = false,
                                company = companyInfo.data,
                                stockIntradayInfos = intradayInfo.data ?: emptyList(),
                                error = null,
                            )
                        else -> it.copy(isLoading = false, error = "An unexpected error occurred")
                    }
                }
            }
        }
    }
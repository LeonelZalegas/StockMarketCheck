package com.example.stockmarketcheck.mainFeature.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketcheck.core.util.Resource
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel
    @Inject
    constructor(
        private val repository: StockRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(CompanyListingsState())
        val state: StateFlow<CompanyListingsState> = _state.asStateFlow()

        private var searchJob: Job? = null

        init {
            getCompanyListings()
        }

        fun onEvent(event: CompanyListingsEvent) {
            when (event) {
                is CompanyListingsEvent.Refresh -> {
                    getCompanyListings(fetchFromRemote = true)
                }
                is CompanyListingsEvent.OnSearchQueryChange -> {
                    _state.update { it.copy(searchQuery = event.query) }
                    searchJob?.cancel()
                    searchJob =
                        viewModelScope.launch {
                            delay(500L)
                            getCompanyListings()
                        }
                }
            }
        }

        private fun getCompanyListings(
            query: String = state.value.searchQuery.lowercase(),
            fetchFromRemote: Boolean = false,
        ) {
            viewModelScope.launch {
                repository
                    .getCompanyListings(fetchFromRemote, query)
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { listings ->
                                    _state.update { it.copy(companies = listings) }
                                }
                            }
                            is Resource.Error -> Unit
                            is Resource.Loading -> {
                                _state.update { it.copy(isLoading = result.isLoading) }
                            }
                        }
                    }
            }
        }
    }
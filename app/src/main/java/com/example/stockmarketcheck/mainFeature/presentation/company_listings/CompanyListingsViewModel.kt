package com.example.stockmarketcheck.mainFeature.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import com.example.stockmarketcheck.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel
    @Inject
    constructor(
        private val repository: StockRepository,
    ) : ViewModel() {
        // el estado es del tipo CompanyListingsState
        var state by mutableStateOf(CompanyListingsState())

        private var searchJob: Job? = null

        fun onEvent(event: CompanyListingsEvent) {
            when (event) {
                is CompanyListingsEvent.Refresh -> {
                    getCompanyListings(fetchFromRemote = true)
                }
                is CompanyListingsEvent.OnSearchQueryChange -> { // esto se triggea con cada letra que escribamos
                    state = state.copy(searchQuery = event.query)
                    searchJob?.cancel()
                    searchJob =
                        viewModelScope.launch {
                            delay(500L)
                            getCompanyListings()
                        }
                } // https://www.notion.so/StockMarket-app-fd555472e30c45ef8586565dc35d7d42?pvs=4#03a95fa091684af5b925f58e961b2c91
            }
        }

        private fun getCompanyListings(
            query: String = state.searchQuery.lowercase(),
            fetchFromRemote: Boolean = false,
        ) {
            viewModelScope.launch {
                repository
                    .getCompanyListings(fetchFromRemote, query)
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { listings ->
                                    state =
                                        state.copy(
                                            companies = listings,
                                        )
                                }
                            } // https://www.notion.so/StockMarket-app-fd555472e30c45ef8586565dc35d7d42?pvs=4#302e2f3f8f404d058bd758d15ed998b8
                            is Resource.Error -> Unit
                            is Resource.Loading -> {
                                state = state.copy(isLoading = result.isLoading) // aca hace referencia a la clase especifica de Loading
                            } // (ya q el campo isLoading es especifico de Loading y no es algo q hereda de Resources )
                        } // es como el field Breed de perro, aca le hace un Smart Cast por eso no se ve bien
                    }
            }
        }
    }
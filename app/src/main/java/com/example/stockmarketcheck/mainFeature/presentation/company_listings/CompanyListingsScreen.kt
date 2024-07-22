package com.example.stockmarketcheck.mainFeature.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CompanyListingsScreen(viewModel: CompanyListingsViewModel = hiltViewModel()) {
    val state = viewModel.state
    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = state.isRefreshing,
            onRefresh = {
                viewModel.onEvent(CompanyListingsEvent.Refresh)
            },
        )

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = systemBarsPadding.calculateTopPadding()),
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = {
                viewModel.onEvent(
                    CompanyListingsEvent.OnSearchQueryChange(it),
                )
            },
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            placeholder = {
                Text(text = "Search...")
            },
            maxLines = 1,
            singleLine = true,
        )
        Box(Modifier.pullRefresh(pullRefreshState)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                if (state.isLoading) {
                    // Show 10 shimmer items when loading
                    items(10) {
                        CompanyItem(
                            company = null,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            isLoading = true,
                        )
                        if (it < 9) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                } else {
                    // Show actual items when not loading
                    items(state.companies.size) { i ->
                        CompanyItem(
                            company = state.companies[i],
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // TODO: Handle click
                                    }
                                    .padding(16.dp),
                            isLoading = false,
                        )
                        if (i < state.companies.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
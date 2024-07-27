package com.example.stockmarketcheck.mainFeature.presentation.company_listings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CompanyListingsScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun companyListingsScreen_displaysCompanies() {
        val companies =
            listOf(
                CompanyListing(name = "Apple Inc.", symbol = "AAPL", exchange = "NASDAQ"),
                CompanyListing(name = "Alphabet Inc.", symbol = "GOOGL", exchange = "NASDAQ"),
                CompanyListing(name = "Microsoft Corporation", symbol = "MSFT", exchange = "NASDAQ"),
            )
        val state =
            CompanyListingsState(
                companies = companies,
                isLoading = false,
                isRefreshing = false,
                searchQuery = "",
            )

        composeTestRule.setContent {
            CompanyListingsScreen(
                state = state,
                onEvent = {},
                onCompanyClick = {},
            )
        }

        companies.forEach { company ->
            composeTestRule.onNodeWithText(company.name).assertIsDisplayed()
        }
    }

    @Test
    fun companyListingsScreen_searchFilterWorks() {
        val companies =
            listOf(
                CompanyListing(name = "Apple Inc.", symbol = "AAPL", exchange = "NASDAQ"),
                CompanyListing(name = "Alphabet Inc.", symbol = "GOOGL", exchange = "NASDAQ"),
                CompanyListing(name = "Microsoft Corporation", symbol = "MSFT", exchange = "NASDAQ"),
            )
        val state =
            CompanyListingsState(
                companies = companies,
                isLoading = false,
                isRefreshing = false,
                searchQuery = "",
            )

        var lastEvent: CompanyListingsEvent? = null

        composeTestRule.setContent {
            CompanyListingsScreen(
                state = state,
                onEvent = { event -> lastEvent = event },
                onCompanyClick = {},
            )
        }

        // Find the TextField and perform input
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Apple")

        // Assert that the correct event was triggered
        assert(lastEvent is CompanyListingsEvent.OnSearchQueryChange)
        assert((lastEvent as CompanyListingsEvent.OnSearchQueryChange).query == "Apple")
    }
}
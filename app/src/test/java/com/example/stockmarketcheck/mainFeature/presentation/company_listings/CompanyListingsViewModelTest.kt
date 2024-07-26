package com.example.stockmarketcheck.mainFeature.presentation.company_listings

import com.example.stockmarketcheck.core.util.Resource
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CompanyListingsViewModelTest {
    private lateinit var viewModel: CompanyListingsViewModel
    private lateinit var repository: StockRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        // Mock all possible combinations
        coEvery { repository.getCompanyListings(eq(false), any()) } returns flowOf(Resource.Success(emptyList()))
        coEvery { repository.getCompanyListings(eq(true), any()) } returns flowOf(Resource.Success(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init fetches company listings`() =
        runTest {
            // Arrange
            val companyListings = listOf(CompanyListing("AAPL", "Apple Inc.", "US"))
            coEvery { repository.getCompanyListings(false, "") } returns flowOf(Resource.Success(companyListings))

            // Act
            viewModel = CompanyListingsViewModel(repository)
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            assertEquals(companyListings, viewModel.state.companies)
            assertFalse(viewModel.state.isLoading)

            coVerify { repository.getCompanyListings(false, "") }
        }

    @Test
    fun `onEvent Refresh fetches company listings from remote`() =
        runTest {
            // Arrange
            val companyListings = listOf(CompanyListing("AAPL", "Apple Inc.", "US"))
            coEvery { repository.getCompanyListings(true, "") } returns flowOf(Resource.Success(companyListings))
            viewModel = CompanyListingsViewModel(repository)

            // Act
            viewModel.onEvent(CompanyListingsEvent.Refresh)
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            assertEquals(companyListings, viewModel.state.companies)
            assertFalse(viewModel.state.isLoading)

            coVerify { repository.getCompanyListings(true, "") }
        }

    @Test
    fun `onEvent OnSearchQueryChange updates search query and fetches listings`() =
        runTest {
            // Arrange
            val companyListings = listOf(CompanyListing("AAPL", "Apple Inc.", "US"))
            coEvery { repository.getCompanyListings(false, "apple") } returns flowOf(Resource.Success(companyListings))
            viewModel = CompanyListingsViewModel(repository)

            // Act
            viewModel.onEvent(CompanyListingsEvent.OnSearchQueryChange("Apple"))
            testDispatcher.scheduler.advanceTimeBy(510) // Wait for debounce

            // Assert
            assertEquals("Apple", viewModel.state.searchQuery)
            assertEquals(companyListings, viewModel.state.companies)

            coVerify { repository.getCompanyListings(false, "apple") }
        }

    @Test
    fun `getCompanyListings handles loading state`() =
        runTest {
            // Arrange
            coEvery { repository.getCompanyListings(false, "") } returns
                flowOf(
                    Resource.Loading(true),
                    Resource.Loading(false),
                    Resource.Success(emptyList()),
                )

            // Act
            viewModel = CompanyListingsViewModel(repository)
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            assertFalse(viewModel.state.isLoading)
            assertTrue(viewModel.state.companies.isEmpty())

            coVerify { repository.getCompanyListings(false, "") }
        }
}
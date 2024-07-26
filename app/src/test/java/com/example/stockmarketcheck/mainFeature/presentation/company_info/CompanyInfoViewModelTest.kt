package com.example.stockmarketcheck.mainFeature.presentation.company_info

import androidx.lifecycle.SavedStateHandle
import com.example.stockmarketcheck.core.util.Resource
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyInfo
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class CompanyInfoViewModelTest {
    private lateinit var viewModel: CompanyInfoViewModel
    private lateinit var repository: StockRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        savedStateHandle =
            SavedStateHandle().apply {
                set("symbol", "AAPL")
            }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init fetches company info and intraday info successfully`() =
        runTest {
            // Arrange
            val companyInfo = CompanyInfo("AAPL", "Apple Inc.", "USA", "1234", "Tech")
            val intradayInfo = listOf(IntradayInfo(LocalDateTime.of(2023, 7, 25, 12, 0), 150.0))

            coEvery { repository.getCompanyInfo("AAPL") } returns Resource.Success(companyInfo)
            coEvery { repository.getIntradayInfo("AAPL") } returns Resource.Success(intradayInfo)

            // Act
            viewModel = CompanyInfoViewModel(savedStateHandle, repository)
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            assertEquals(companyInfo, viewModel.state.company)
            assertEquals(intradayInfo, viewModel.state.stockIntradayInfos)
            assertFalse(viewModel.state.isLoading)
            assertNull(viewModel.state.error)

            coVerify { repository.getCompanyInfo("AAPL") }
            coVerify { repository.getIntradayInfo("AAPL") }
        }

    @Test
    fun `init handles company info error`() =
        runTest {
            // Arrange
            coEvery { repository.getCompanyInfo("AAPL") } returns Resource.Error("Couldn't load company info", null)
            coEvery { repository.getIntradayInfo("AAPL") } returns Resource.Success(emptyList())

            // Act
            viewModel = CompanyInfoViewModel(savedStateHandle, repository)
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            assertNull(viewModel.state.company)
            assertTrue(viewModel.state.stockIntradayInfos.isEmpty())
            assertFalse(viewModel.state.isLoading)
            assertEquals("Couldn't load company info", viewModel.state.error)

            coVerify { repository.getCompanyInfo("AAPL") }
            coVerify { repository.getIntradayInfo("AAPL") }
        }

    @Test
    fun `init handles intraday info error`() =
        runTest {
            // Arrange
            val companyInfo = CompanyInfo("AAPL", "Apple Inc.", "USA", "1234", "Tech")
            coEvery { repository.getCompanyInfo("AAPL") } returns Resource.Success(companyInfo)
            coEvery { repository.getIntradayInfo("AAPL") } returns Resource.Error("Error fetching intraday info", null)

            // Act
            viewModel = CompanyInfoViewModel(savedStateHandle, repository)
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            assertEquals(companyInfo, viewModel.state.company)
            assertTrue(viewModel.state.stockIntradayInfos.isEmpty())
            assertFalse(viewModel.state.isLoading)
            assertEquals("Error fetching intraday info", viewModel.state.error)
        }
}
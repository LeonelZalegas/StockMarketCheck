package com.example.stockmarketcheck.mainFeature.data.repository

import com.example.stockmarketcheck.core.util.Resource
import com.example.stockmarketcheck.mainFeature.data.csv.CSVParser
import com.example.stockmarketcheck.mainFeature.data.local.CompanyListingEntity
import com.example.stockmarketcheck.mainFeature.data.local.StockDao
import com.example.stockmarketcheck.mainFeature.data.local.StockDatabase
import com.example.stockmarketcheck.mainFeature.data.mapper.toCompanyListing
import com.example.stockmarketcheck.mainFeature.data.mapper.toCompanyListingEntity
import com.example.stockmarketcheck.mainFeature.data.remote.StockClient
import com.example.stockmarketcheck.mainFeature.data.remote.dto.CompanyInfoDto
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyInfo
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import io.mockk.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime

class StockRepositoryImplTest {
    // Mock dependencies
    private lateinit var mockApi: StockClient
    private lateinit var mockDb: StockDatabase
    private lateinit var mockDao: StockDao
    private lateinit var mockCompanyListingsParser: CSVParser<CompanyListing>
    private lateinit var mockIntradayInfoParser: CSVParser<IntradayInfo>

    // System under test
    private lateinit var repository: StockRepositoryImpl

    @Before
    fun setup() {
        mockApi = mockk()
        mockDb = mockk()
        mockDao = mockk()
        mockCompanyListingsParser = mockk()
        mockIntradayInfoParser = mockk()

        every { mockDb.dao } returns mockDao

        repository =
            StockRepositoryImpl(
                api = mockApi,
                db = mockDb,
                companyListingsParser = mockCompanyListingsParser,
                intradayInfoParser = mockIntradayInfoParser,
            )
    }

    @Test
    fun `getCompanyListings returns success when api call is successful (fetchFromRemote = true)`() =
        runBlocking {
            // Arrange
            val query = ""
            val mockResponseBody = "mock csv data".toResponseBody()
            val mockCompanyListings = listOf(CompanyListing("AAPL", "Apple Inc.", "US"))
            val mockCompanyListingEntities = mockCompanyListings.map { it.toCompanyListingEntity() }

            coEvery { mockApi.getListings() } returns mockResponseBody
            coEvery { mockCompanyListingsParser.parse(any()) } returns mockCompanyListings
            // el andThen significa q luego del emptyList (q es la 1ra llamada) todas las llamadas son mockCompanyListingEntities
            coEvery { mockDao.searchCompanyListings(query) } returns emptyList() andThen mockCompanyListingEntities
            // is used for void functions or suspend functions that don't return anything. It tells MockK that the function should be mocked to do nothing
            // (just run without any side effects or returns) they just perform an action.
            coEvery { mockDao.clearCompanyListings() } just Runs
            coEvery { mockDao.insertCompanyListings(any()) } just Runs

            // Act
            val results = repository.getCompanyListings(fetchFromRemote = true, query = query).toList()

            // Assert
            assertEquals(4, results.size)
            assertTrue(results[0] is Resource.Loading)
            assertTrue(results[1] is Resource.Success) // Initial Success (el search vacio porq query es "")
            assertTrue(results[2] is Resource.Success) // Final Success (list from remote API)
            assertTrue(results[3] is Resource.Loading)

            val initialSuccess = results[1] as Resource.Success
            val finalSuccess = results[2] as Resource.Success
            assertEquals(emptyList<CompanyListing>(), initialSuccess.data)
            assertEquals(mockCompanyListings, finalSuccess.data)

            assertTrue((results[0] as Resource.Loading).isLoading)
            assertFalse((results[3] as Resource.Loading).isLoading)

            // Verify interactions
            // used to check that a certain function was called during the test. It's part of behavior verification in testing
            coVerify { mockApi.getListings() }
            coVerify { mockCompanyListingsParser.parse(any()) }
            coVerify { mockDao.clearCompanyListings() }
            coVerify { mockDao.insertCompanyListings(any()) }
        }

    @Test
    fun `getCompanyListings returns error when api call fails (fetchFromRemote = true)`() =
        runBlocking {
            // Arrange
            val query = ""
            coEvery { mockApi.getListings() } throws IOException("Network error")
            coEvery { mockDao.searchCompanyListings(query) } returns emptyList()

            // Act
            val results = repository.getCompanyListings(fetchFromRemote = true, query = query).toList()

            // Assert
            assertEquals(3, results.size)
            assertTrue(results[0] is Resource.Loading)
            assertTrue(results[1] is Resource.Success)
            assertTrue(results[2] is Resource.Error)

            val successResult = results[1] as Resource.Success
            assertEquals(emptyList<CompanyListing>(), successResult.data)

            val errorResult = results[2] as Resource.Error
            assertEquals("Couldn't load data", errorResult.message)

            // Verify interactions
            coVerify { mockApi.getListings() }
            coVerify(exactly = 0) { mockDao.clearCompanyListings() }
            coVerify(exactly = 0) { mockDao.insertCompanyListings(any()) }
        }

    @Test
    fun `getCompanyListings returns cached data when fetchFromRemote is false`() =
        runBlocking {
            // Arrange
            val query = ""
            val cachedListings = listOf(CompanyListingEntity("AAPL", "Apple Inc.", "US"))
            coEvery { mockDao.searchCompanyListings(query) } returns cachedListings

            // Act
            val results = repository.getCompanyListings(fetchFromRemote = false, query = query).toList()

            // Assert
            assertEquals(3, results.size)
            assertTrue(results[0] is Resource.Loading)
            assertTrue(results[1] is Resource.Success)
            assertTrue(results[2] is Resource.Loading)

            val successResult = results[1] as Resource.Success
            assertEquals(cachedListings.map { it.toCompanyListing() }, successResult.data)

            // Verify interactions
            coVerify(exactly = 0) { mockApi.getListings() }
            coVerify(exactly = 0) { mockDao.clearCompanyListings() }
            coVerify(exactly = 0) { mockDao.insertCompanyListings(any()) }
        }

    @Test
    fun `getCompanyListings fetches remote data when local db is empty`() =
        runBlocking {
            // Arrange
            val query = ""
            val mockResponseBody = "mock csv data".toResponseBody()
            val mockCompanyListings = listOf(CompanyListing("AAPL", "Apple Inc.", "US"))
            val mockCompanyListingEntities = mockCompanyListings.map { it.toCompanyListingEntity() }

            coEvery { mockDao.searchCompanyListings(query) } returns emptyList() andThen mockCompanyListingEntities
            coEvery { mockApi.getListings() } returns mockResponseBody
            coEvery { mockCompanyListingsParser.parse(any()) } returns mockCompanyListings
            coEvery { mockDao.clearCompanyListings() } just Runs
            coEvery { mockDao.insertCompanyListings(any()) } just Runs

            // Act
            val results = repository.getCompanyListings(fetchFromRemote = false, query = query).toList()

            // Assert
            assertEquals(4, results.size)
            assertTrue(results[0] is Resource.Loading)
            assertTrue(results[1] is Resource.Success)
            assertTrue(results[2] is Resource.Success)
            assertTrue(results[3] is Resource.Loading)

            val initialSuccess = results[1] as Resource.Success
            val finalSuccess = results[2] as Resource.Success
            assertEquals(emptyList<CompanyListing>(), initialSuccess.data)
            assertEquals(mockCompanyListings, finalSuccess.data)

            assertTrue((results[0] as Resource.Loading).isLoading)
            assertFalse((results[3] as Resource.Loading).isLoading)

            // Verify interactions
            coVerify { mockApi.getListings() }
            coVerify { mockCompanyListingsParser.parse(any()) }
            coVerify { mockDao.clearCompanyListings() }
            coVerify { mockDao.insertCompanyListings(any()) }
        }

    @Test
    fun `getCompanyListings filters results when query is not empty`() =
        runBlocking {
            // Arrange
            val query = "App"
            val allListings =
                listOf(
                    CompanyListingEntity("AAPL", "Apple Inc.", "US"),
                    CompanyListingEntity("MSFT", "Microsoft Corporation", "US"),
                    CompanyListingEntity("GOOGL", "Alphabet Inc.", "US"),
                )
            val filteredListings =
                listOf(
                    CompanyListingEntity("AAPL", "Apple Inc.", "US"),
                )

            // Mock the DAO to return all listings for empty query and filtered for non-empty
            coEvery { mockDao.searchCompanyListings("") } returns allListings
            coEvery { mockDao.searchCompanyListings(query) } returns filteredListings

            // Act
            val results = repository.getCompanyListings(fetchFromRemote = false, query = query).toList()

            // Assert
            assertEquals(3, results.size)
            assertTrue(results[0] is Resource.Loading)
            assertTrue(results[1] is Resource.Success)
            assertTrue(results[2] is Resource.Loading)

            val successResult = results[1] as Resource.Success
            assertEquals(filteredListings.map { it.toCompanyListing() }, successResult.data)

            // Verify interactions
            coVerify { mockDao.searchCompanyListings(query) }
            coVerify(exactly = 0) { mockApi.getListings() }

            // Verify that with empty query, we get all listings
            val resultsEmptyQuery = repository.getCompanyListings(fetchFromRemote = false, query = "").toList()
            val successResultEmptyQuery = resultsEmptyQuery[1] as Resource.Success
            assertEquals(allListings.map { it.toCompanyListing() }, successResultEmptyQuery.data)
        }

    @Test
    fun `getIntradayInfo returns success when api call is successful`() =
        runBlocking {
            // Arrange
            val symbol = "AAPL"
            val mockResponseBody = "mock csv data".toResponseBody()
            val mockIntradayInfoList = listOf(IntradayInfo(LocalDateTime.now(), 150.0))

            coEvery { mockApi.getIntradayInfo(symbol) } returns mockResponseBody
            coEvery { mockIntradayInfoParser.parse(any()) } returns mockIntradayInfoList

            // Act
            val result = repository.getIntradayInfo(symbol)

            // Assert
            assertTrue(result is Resource.Success)
            assertEquals(mockIntradayInfoList, (result as Resource.Success).data)

            // Verify interactions
            coVerify { mockApi.getIntradayInfo(symbol) }
            coVerify { mockIntradayInfoParser.parse(any()) }
        }

    @Test
    fun `getIntradayInfo returns error when api call fails`() =
        runBlocking {
            // Arrange
            val symbol = "AAPL"
            coEvery { mockApi.getIntradayInfo(symbol) } throws IOException("Network error")

            // Act
            val result = repository.getIntradayInfo(symbol)

            // Assert
            assertTrue(result is Resource.Error)
            assertEquals("Couldn't load intraday info", (result as Resource.Error).message)

            // Verify interactions
            coVerify { mockApi.getIntradayInfo(symbol) }
        }

    @Test
    fun `getCompanyInfo returns success when api call is successful`() =
        runBlocking {
            // Arrange
            val symbol = "AAPL"
            val mockCompanyInfoDto =
                CompanyInfoDto(
                    symbol = "AAPL",
                    description = "Apple Inc.",
                    name = "Apple",
                    country = "USA",
                    industry = "Technology",
                )
            coEvery { mockApi.getCompanyInfo(symbol) } returns mockCompanyInfoDto

            // Act
            val result = repository.getCompanyInfo(symbol)

            // Assert
            assertTrue(result is Resource.Success)
            val expectedCompanyInfo =
                CompanyInfo(
                    symbol = "AAPL",
                    description = "Apple Inc.",
                    name = "Apple",
                    country = "USA",
                    industry = "Technology",
                )
            assertEquals(expectedCompanyInfo, (result as Resource.Success).data)

            // Verify interactions
            coVerify { mockApi.getCompanyInfo(symbol) }
        }

    @Test
    fun `getCompanyInfo returns error when api call fails`() =
        runBlocking {
            // Arrange
            val symbol = "AAPL"
            coEvery { mockApi.getCompanyInfo(symbol) } throws IOException("Network error")

            // Act
            val result = repository.getCompanyInfo(symbol)

            // Assert
            assertTrue(result is Resource.Error)
            assertEquals("Couldn't load company info", (result as Resource.Error).message)

            // Verify interactions
            coVerify { mockApi.getCompanyInfo(symbol) }
        }
}
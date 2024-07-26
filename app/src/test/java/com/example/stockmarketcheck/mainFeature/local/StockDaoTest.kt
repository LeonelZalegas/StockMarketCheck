package com.example.stockmarketcheck.mainFeature.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.stockmarketcheck.mainFeature.data.local.CompanyListingEntity
import com.example.stockmarketcheck.mainFeature.data.local.StockDao
import com.example.stockmarketcheck.mainFeature.data.local.StockDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class StockDaoTest {
    private lateinit var database: StockDatabase
    private lateinit var dao: StockDao

    @Before
    fun setup() {
        // Create an in-memory database for testing
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                StockDatabase::class.java,
            ).allowMainThreadQueries().build()
        dao = database.dao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveCompanyListings() =
        runBlocking {
            // Prepare test data
            val companies =
                listOf(
                    CompanyListingEntity("Apple Inc.", "AAPL", "NASDAQ"),
                    CompanyListingEntity("Google", "GOOGL", "NASDAQ"),
                )

            // Insert data
            dao.insertCompanyListings(companies)

            // Retrieve data
            val retrievedCompanies = dao.searchCompanyListings("")

            // Assert
            assertEquals(2, retrievedCompanies.size)
            assertEquals("Apple Inc.", retrievedCompanies[0].name)
            assertEquals("AAPL", retrievedCompanies[0].symbol)
            assertEquals("NASDAQ", retrievedCompanies[0].exchange)
            assertNotNull(retrievedCompanies[0].id)
            assertEquals("Google", retrievedCompanies[1].name)
            assertEquals("GOOGL", retrievedCompanies[1].symbol)
            assertEquals("NASDAQ", retrievedCompanies[1].exchange)
            assertNotNull(retrievedCompanies[1].id)
        }

    @Test
    fun searchCompanyListings() =
        runBlocking {
            // Prepare and insert test data
            val companies =
                listOf(
                    CompanyListingEntity("Apple Inc.", "AAPL", "NASDAQ"),
                    CompanyListingEntity("Google", "GOOGL", "NASDAQ"),
                    CompanyListingEntity("Microsoft", "MSFT", "NASDAQ"),
                )
            dao.insertCompanyListings(companies)

            // Apple search
            val appleSearch = dao.searchCompanyListings("apple")
            assertEquals(1, appleSearch.size)
            assertEquals("Apple Inc.", appleSearch[0].name)

            // Google search
            val googleSearch = dao.searchCompanyListings("GOOGL")
            assertEquals(1, googleSearch.size)
            assertEquals("Google", googleSearch[0].name)

            // Microsoft search
            val softSearch = dao.searchCompanyListings("soft")
            assertEquals(1, softSearch.size)
            assertEquals("Microsoft", softSearch[0].name)

            // No match search
            val noMatch = dao.searchCompanyListings("xyz")
            assertEquals(0, noMatch.size)

            // Case insensitive search
            val caseInsensitiveSearch = dao.searchCompanyListings("gOoGl")
            assertEquals(1, caseInsensitiveSearch.size)
            assertEquals("Google", caseInsensitiveSearch[0].name)
        }

    @Test
    fun clearCompanyListings() =
        runBlocking {
            // Prepare and insert test data
            val companies =
                listOf(
                    CompanyListingEntity("Apple Inc.", "AAPL", "NASDAQ"),
                    CompanyListingEntity("Google", "GOOGL", "NASDAQ"),
                )
            dao.insertCompanyListings(companies)

            // Verify data was inserted
            var retrievedCompanies = dao.searchCompanyListings("")
            assertEquals(2, retrievedCompanies.size)

            // Clear the database
            dao.clearCompanyListings()

            // Verify that the database is empty
            retrievedCompanies = dao.searchCompanyListings("")
            assertEquals(0, retrievedCompanies.size)
        }
}
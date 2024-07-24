package com.example.stockmarketcheck.mainFeature.data.csv

import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.opencsv.CSVReader
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream
import java.io.InputStream

@ExperimentalCoroutinesApi
class CompanyListingsParserTest {
    private lateinit var parser: CompanyListingsParser
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set up the test dispatcher for coroutines
        Dispatchers.setMain(testDispatcher)
        parser = CompanyListingsParser()
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher after the test
        Dispatchers.resetMain()
    }

    @Test
    fun `parse valid CSV input`() =
        runTest {
            // Prepare test data
            val csvData =
                """
                Symbol,Name,Exchange
                AAPL,Apple Inc.,NASDAQ
                GOOGL,Alphabet Inc.,NASDAQ
                """.trimIndent()
            val inputStream: InputStream = ByteArrayInputStream(csvData.toByteArray())

            // Mock the CSVReader to return our test data
            mockkStatic("com.opencsv.CSVReaderBuilder")
            mockkConstructor(CSVReader::class)
            every { anyConstructed<CSVReader>().readAll() } returns
                listOf(
                    arrayOf("Symbol", "Name", "Exchange"),
                    arrayOf("AAPL", "Apple Inc.", "NASDAQ"),
                    arrayOf("GOOGL", "Alphabet Inc.", "NASDAQ"),
                )

            // Call the method under test
            val result = parser.parse(inputStream)

            // Assert the results
            assertEquals(2, result.size)
            assertEquals(CompanyListing("Apple Inc.", "AAPL", "NASDAQ"), result[0])
            assertEquals(CompanyListing("Alphabet Inc.", "GOOGL", "NASDAQ"), result[1])
        }

    @Test
    fun `parse CSV with missing fields`() =
        runTest {
            // Prepare test data with missing fields
            val csvData =
                """
                Symbol,Name,Exchange
                AAPL,Apple Inc.,
                ,Alphabet Inc.,NASDAQ
                """.trimIndent()
            val inputStream: InputStream = ByteArrayInputStream(csvData.toByteArray())

            // Mock the CSVReader to return our test data
            mockkStatic("com.opencsv.CSVReaderBuilder")
            mockkConstructor(CSVReader::class)
            every { anyConstructed<CSVReader>().readAll() } returns
                listOf(
                    arrayOf("Symbol", "Name", "Exchange"),
                    arrayOf("AAPL", "Apple Inc.", null),
                    arrayOf(null, "Alphabet Inc.", "NASDAQ"),
                )

            // Call the method under test
            val result = parser.parse(inputStream)

            // Assert the results
            assertEquals(0, result.size) // Both entries should be skipped due to missing fields
        }
}
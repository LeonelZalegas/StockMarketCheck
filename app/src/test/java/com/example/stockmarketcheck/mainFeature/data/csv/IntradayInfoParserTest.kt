package com.example.stockmarketcheck.mainFeature.data.csv

import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import com.opencsv.CSVReader
import io.mockk.every
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
class IntradayInfoParserTest {
    private lateinit var parser: IntradayInfoParser
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        parser = IntradayInfoParser()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `parse valid CSV input`() =
        runTest {
            // Get yesterday's date for filtering
            val yesterday = LocalDate.now().minusDays(1)
            val yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // Prepare test data
            val csvData =
                """
                timestamp,open,high,low,close,volume
                $yesterdayStr 10:00:00,100.0,101.0,99.0,100.5,1000
                $yesterdayStr 11:00:00,100.5,102.0,100.0,101.5,1500
                ${LocalDate.now()} 10:00:00,101.5,103.0,101.0,102.5,2000
                """.trimIndent()
            val inputStream: InputStream = ByteArrayInputStream(csvData.toByteArray())

            // Mock the CSVReader
            mockkStatic("com.opencsv.CSVReaderBuilder")
            every { anyConstructed<CSVReader>().readAll() } returns
                listOf(
                    arrayOf("timestamp", "open", "high", "low", "close", "volume"),
                    arrayOf("$yesterdayStr 10:00:00", "100.0", "101.0", "99.0", "100.5", "1000"),
                    arrayOf("$yesterdayStr 11:00:00", "100.5", "102.0", "100.0", "101.5", "1500"),
                    arrayOf("${LocalDate.now()} 10:00:00", "101.5", "103.0", "101.0", "102.5", "2000"),
                )

            // Call the method under test
            val result = parser.parse(inputStream)

            // Assert the results
            assertEquals(2, result.size) // Only yesterday's data should be included
            assertEquals(
                IntradayInfo(LocalDateTime.parse("$yesterdayStr 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 100.5),
                result[0],
            )
            assertEquals(
                IntradayInfo(LocalDateTime.parse("$yesterdayStr 11:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 101.5),
                result[1],
            )
        }

    @Test
    fun `parse CSV with invalid data`() =
        runTest {
            // Prepare test data with invalid entries
            val csvData =
                """
                timestamp,open,high,low,close,volume
                invalid_date,100.0,101.0,99.0,100.5,1000
                ${LocalDate.now().minusDays(1)} 10:00:00,100.5,102.0,100.0,invalid_close,1500
                """.trimIndent()
            val inputStream: InputStream = ByteArrayInputStream(csvData.toByteArray())

            // Mock the CSVReader
            mockkStatic("com.opencsv.CSVReaderBuilder")
            every { anyConstructed<CSVReader>().readAll() } returns
                listOf(
                    arrayOf("timestamp", "open", "high", "low", "close", "volume"),
                    arrayOf("invalid_date", "100.0", "101.0", "99.0", "100.5", "1000"),
                    arrayOf("${LocalDate.now().minusDays(1)} 10:00:00", "100.5", "102.0", "100.0", "invalid_close", "1500"),
                )

            // Call the method under test
            val result = parser.parse(inputStream)

            // Assert the results
            assertEquals(0, result.size) // Both entries should be skipped due to invalid data
        }
}
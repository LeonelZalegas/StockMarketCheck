package com.example.stockmarketcheck.mainFeature.data.csv

import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser
    @Inject
    constructor() : CSVParser<IntradayInfo> {
        override suspend fun parse(stream: InputStream): List<IntradayInfo> {
            val csvReader = CSVReader(InputStreamReader(stream))
            return withContext(Dispatchers.IO) {
                csvReader
                    .readAll()
                    .drop(1)
                    .mapNotNull { line ->
                        val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                        val close = line.getOrNull(4) ?: return@mapNotNull null
                        val date = convertToLocalDateTime(timestamp) ?: return@mapNotNull null
                        try {
                            IntradayInfo(
                                date = date,
                                close = close.toDouble(),
                            )
                        } catch (e: NumberFormatException) {
                            null // Return null if close price is not a valid double
                        }
                    } // Aca ya tenemos la lista de IntradayInfo pero a esto filtramos y Guardamos solo
                    .filter { // los IntradayInfo con campo date = a la fecha de ayer de nuetra maquina
                        it.date.dayOfMonth == LocalDate.now().minusDays(1).dayOfMonth
                    }
                    .sortedBy {
                        it.date.hour
                    }
                    .also {
                        csvReader.close()
                    }
            }
        }

        private fun convertToLocalDateTime(timestamp: String): LocalDateTime? {
            return try {
                val pattern = "yyyy-MM-dd HH:mm:ss"
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                LocalDateTime.parse(timestamp, formatter)
            } catch (e: DateTimeParseException) {
                null // Return null if parsing fails
            }
        }
    }
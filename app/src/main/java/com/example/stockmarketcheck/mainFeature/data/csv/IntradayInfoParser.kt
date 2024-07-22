package com.example.stockmarketcheck.mainFeature.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser
    @Inject
    constructor() : CSVParser<IntradayInfo> {
        @RequiresApi(Build.VERSION_CODES.O)
        override suspend fun parse(stream: InputStream): List<IntradayInfo> {
            val csvReader = CSVReader(InputStreamReader(stream))
            return withContext(Dispatchers.IO) {
                csvReader
                    .readAll()
                    .drop(1)
                    .mapNotNull { line ->
                        val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                        val close = line.getOrNull(4) ?: return@mapNotNull null
                        IntradayInfo(
                            date = convertToLocalDateTime(timestamp),
                            close = close.toDouble(),
                        )
                    } // Aca ya tenemos la lista de IntradayInfo pero a esto filtramos y Guardamos solo
                    .filter { // los IntradayInfo con campo date = a la fecha de ayer de nuetra maquina
                        it.date.dayOfMonth == LocalDate.now().minusDays(4).dayOfMonth
                    }
                    .sortedBy {
                        it.date.hour
                    }
                    .also {
                        csvReader.close()
                    }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun convertToLocalDateTime(timestamp: String): LocalDateTime {
            val pattern = "yyyy-MM-dd HH:mm:ss"
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            val localDateTime = LocalDateTime.parse(timestamp, formatter)
            return localDateTime
        }
    }
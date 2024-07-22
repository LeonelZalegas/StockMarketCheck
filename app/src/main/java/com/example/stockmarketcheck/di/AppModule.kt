package com.example.stockmarketcheck.di

import android.app.Application
import androidx.room.Room
import com.example.stockmarketcheck.mainFeature.data.local.StockDatabase
import com.example.stockmarketcheck.mainFeature.data.remote.StockClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideStockApi(): StockClient {
        return Retrofit.Builder()
            .baseUrl("https://alphavantage.co")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideStockDatabase(app: Application): StockDatabase {
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            "stockdb.db",
        ).build()
    }
}
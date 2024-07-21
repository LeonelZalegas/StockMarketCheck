package com.example.stockmarketcheck.mainFeature.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stockmarketcheck.mainFeature.presentation.company_listings.CompanyListingsScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = CompanyListings::class) {
        composable<CompanyListings> {
            CompanyListingsScreen()
        }
        // More composable destinations can be added here later
    }
}

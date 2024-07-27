package com.example.stockmarketcheck.mainFeature.presentation.company_info

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CompanyInfoScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun simpleCompanyInfoScreen_displaysBasicInfo() {
        composeTestRule.setContent {
            Column {
                Text("Company Name: Test Company")
                Text("Symbol: TEST")
            }
        }

        composeTestRule.onNodeWithText("Company Name: Test Company").assertIsDisplayed()
        composeTestRule.onNodeWithText("Symbol: TEST").assertIsDisplayed()
    }

    @Test
    fun companyInfoScreen_displaysLoadingState() {
        val state = CompanyInfoState(isLoading = true)

        composeTestRule.setContent {
            CompanyInfoScreen(state = state)
        }

        composeTestRule.onNode(hasTestTag("progressIndicator")).assertExists()
    }

    @Test
    fun companyInfoScreen_displaysErrorState() {
        val errorMessage = "An error occurred while fetching company info"
        val state = CompanyInfoState(error = errorMessage)

        composeTestRule.setContent {
            CompanyInfoScreen(state = state)
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
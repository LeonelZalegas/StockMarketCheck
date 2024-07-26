package com.example.stockmarketcheck.mainFeature.presentation.company_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stockmarketcheck.ui.theme.DarkBlue

@Composable
fun CompanyInfoScreen(
    symbol: String,
    viewModel: CompanyInfoViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    if (state.error == null) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(DarkBlue)
                    .padding(top = systemBarsPadding.calculateTopPadding()),
        ) {
            state.company?.let { company ->
                Text(
                    text = company.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = company.symbol,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Industry: ${company.industry}",
                    fontSize = 14.sp,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Country: ${company.country}",
                    fontSize = 14.sp,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = company.description,
                    fontSize = 12.sp,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                )
                if (state.stockIntradayInfos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Market Summary")
                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                    ) {
                        IntradayInfoChart(
                            infos = state.stockIntradayInfos,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                        )
                        Box(
                            modifier =
                                Modifier
                                    .height(300.dp)
                                    .width(20.dp)
                                    .background(DarkBlue)
                                    .align(Alignment.CenterEnd)
                                    .offset(x = (-20).dp)
                                    .zIndex(1f),
                        )
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
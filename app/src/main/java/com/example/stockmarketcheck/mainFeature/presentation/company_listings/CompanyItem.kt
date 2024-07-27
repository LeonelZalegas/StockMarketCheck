package com.example.stockmarketcheck.mainFeature.presentation.company_listings

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing

@Composable
fun CompanyItem(
    company: CompanyListing?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    // https://www.notion.so/StockMarket-app-fd555472e30c45ef8586565dc35d7d42?pvs=4#0e4191b1786e4f08abdc6e979396d041
    // 1. Define the colors for the shimmer effect
    val shimmerColors =
        listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

    // 2. Create an infinite transition
    val transition = rememberInfiniteTransition(label = "")

    // 3. Define the animation
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "",
    )
    // 4. Create the shimmer brush
    val brush =
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim, y = translateAnim),
        )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoading) {
                    Spacer(
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(20.dp)
                                .background(brush),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Spacer(
                        modifier =
                            Modifier
                                .width(60.dp)
                                .height(20.dp)
                                .background(brush),
                    )
                } else {
                    Text(
                        text = company?.name ?: "",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = company?.exchange ?: "",
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                Spacer(
                    modifier =
                        Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .background(brush),
                )
            } else {
                Text(
                    text = "(${company?.symbol ?: ""})",
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
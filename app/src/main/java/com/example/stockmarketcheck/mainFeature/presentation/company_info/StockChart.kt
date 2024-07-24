package com.example.stockmarketcheck.mainFeature.presentation.company_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun IntradayInfoChart(
    infos: List<IntradayInfo>,
    modifier: Modifier = Modifier,
) {
    val points =
        infos.map { info ->
            Point(
                x = info.date.hour.toFloat(),
                y = info.close.toFloat(),
            )
        }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val backgroundColor = MaterialTheme.colorScheme.background

    val upperValue = (infos.maxOfOrNull { it.close }?.plus(1))?.roundToInt() ?: 0
    val lowerValue = infos.minOfOrNull { it.close }?.toInt() ?: 0
    val priceStep = (upperValue - lowerValue) / 5f

    val yAxisData =
        AxisData.Builder()
            .axisStepSize(30.dp)
            .steps(5)
            .labelData { i ->
                (lowerValue + priceStep * i).roundToInt().toString()
            }
            .labelAndAxisLinePadding(15.dp)
            .axisLineColor(onBackgroundColor)
            .axisLabelColor(onBackgroundColor)
            .backgroundColor(backgroundColor)
            .build()

    val xAxisData =
        AxisData.Builder()
            .axisStepSize(50.dp)
            .steps(infos.size - 1)
            .labelData { i ->
                if (i != 0) {
                    infos.getOrNull(i)?.date?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
                } else {
                    infos.getOrNull(i)?.date?.format(DateTimeFormatter.ofPattern("HH")) ?: ""
                }
            }
            .labelAndAxisLinePadding(15.dp)
            .axisLineColor(onBackgroundColor)
            .axisLabelColor(onBackgroundColor)
            .backgroundColor(backgroundColor)
            .build()

    val lineChartData =
        LineChartData(
            linePlotData =
                LinePlotData(
                    lines =
                        listOf(
                            Line(
                                dataPoints = points,
                                lineStyle = LineStyle(color = primaryColor, width = 3f),
                                intersectionPoint = IntersectionPoint(color = primaryColor),
                                selectionHighlightPoint = SelectionHighlightPoint(color = primaryColor),
                                shadowUnderLine = ShadowUnderLine(color = primaryColor),
                                selectionHighlightPopUp = SelectionHighlightPopUp(),
                            ),
                        ),
                ),
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = backgroundColor,
            containerPaddingEnd = (-2).dp,
        )

    LineChart(
        modifier =
            modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(backgroundColor),
        lineChartData = lineChartData,
    )
}

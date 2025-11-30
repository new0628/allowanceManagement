package com.example.allowancemanagement.view.yearStats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowancemanagement.view.monthStats.formatAmountShort

@Composable
fun YearMonthDisplay(year : Int, monthSumMap : Map<Int, Int>){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 8.dp, bottom = 0.dp)
    ){
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
        ) {
            TextTitle("월")
            TextTitle("금액")
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )
        // 1~12월 행
        for (month in 1..12) {
            val sum = monthSumMap[month] ?: 0
            val sumText = if (sum == 0) "0원" else formatAmountShort(sum) + "원"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
            ) {
                TextContent("${month}월")

                TextContent(sumText)
            }

            // 각 행 아래 선
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun RowScope.TextContent(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 4.dp),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun RowScope.TextTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 6.dp),
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary
    )
}
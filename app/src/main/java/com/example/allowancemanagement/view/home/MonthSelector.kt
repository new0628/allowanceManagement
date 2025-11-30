package com.example.allowancemanagement.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

// 년/월 선택 날짜 UI
@Composable
fun MonthSelector(year : Int, month : Int, onMonthChange : (Int, Int) -> Unit) {
    // 오늘 기준
    val today = LocalDate.now()
    val currentYear = today.year
    val currentMonth = today.monthValue

    // 년 / 월 -> 문자열로
    val formatDate = "%04d.%02d".format(year, month)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽 <
        IconButton(
            onClick = {
                val newYear : Int
                val newMonth : Int
                if (month == 1) {
                    newYear = year - 1
                    newMonth = 12
                }
                else {
                    newYear = year
                    newMonth = month - 1
                }
                onMonthChange(newYear, newMonth)
            },
            enabled = !(year == 2024 && month == 1)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "prev month",
                tint = if (year == 2024 && month == 1) {
                    Color.Gray
                } else {
                    Color.Red
                }
            )
        }

        Text(
            text = formatDate,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        // 오른쪽 >
        IconButton(
            onClick = {
                if (!(year == currentYear && month == currentMonth)) {
                    val newYear : Int
                    val newMonth : Int
                    if (month == 12) {
                        newYear = year + 1
                        newMonth = 1
                    }
                    else {
                        newYear = year
                        newMonth = month + 1
                    }
                    onMonthChange(newYear, newMonth)
                }
            },
            enabled = !(year == currentYear && month == currentMonth)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "next month",
                tint = if (year == currentYear && month == currentMonth) {
                    Color.Gray
                } else {
                    Color.Red
                }
            )
        }
    }
}
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

// 년/월 선택 날짜 UI
@Composable
fun MonthSelector() {
    // 오늘 기준
    val today = LocalDate.now()
    val currentYear = today.year
    val currentMonth = today.monthValue

    // 선택된 년/월 (초기값은 현재 년, 월)
    var selectYear by remember { mutableIntStateOf(currentYear) }
    var selectMonth by remember { mutableIntStateOf(currentMonth) }

    // 년 / 월 -> 문자열로
    val formatDate = "%04d.%02d".format(selectYear, selectMonth)

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽 <
        IconButton (
            onClick = {
                if (selectMonth == 1) {
                    selectYear --
                    selectMonth = 12
                }
                else {
                    selectMonth --
                }
            },
            enabled = !(selectYear == 2024 && selectMonth == 1)
        ) {
            Icon (
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "prev month",
                tint = if (selectYear == 2024 && selectMonth == 1) {
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
        IconButton (
            onClick = {
                if (!(selectYear == currentYear && selectMonth == currentMonth)) {
                    if (selectMonth == 12) {
                        selectYear ++
                        selectMonth = 1
                    }
                    else {
                        selectMonth ++
                    }
                }
            },
            enabled = !(selectYear == currentYear && selectMonth == currentMonth)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "next month",
                tint = if (selectYear == currentYear && selectMonth == currentMonth) {
                    Color.Gray
                } else {
                    Color.Red
                }
            )
        }
    }
}
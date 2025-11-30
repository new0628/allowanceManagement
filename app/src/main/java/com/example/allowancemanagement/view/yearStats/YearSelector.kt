package com.example.allowancemanagement.view.yearStats

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

@Composable
fun YearSelector (year : Int, onYearChange : (Int) -> Unit) {
    val currentYear = LocalDate.now().year
    val minYear = 2024
    val formatYear = "%04d".format(year)

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        // <- 버튼
        IconButton (
            onClick = {
                if (year > minYear) {
                    onYearChange(year - 1)
                }
            },
            enabled = year > minYear
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "prev year",
                tint = if (year > minYear) Color.Red else Color.Gray
            )
        }

        // 현재 연도 표시
        Text (
            text = formatYear,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        // -> 버튼
        IconButton(
            onClick = {
                if (year < currentYear) {
                    onYearChange(year + 1)
                }
            },
            enabled = year < currentYear
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "next year",
                tint = if (year < currentYear) Color.Red else Color.Gray
            )
        }
    }
}
package com.example.allowancemanagement.view.monthStats


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun CalendarDisplay (year : Int, month : Int, dailyAmount : Map<Int, Int>, dailyAmountColor : Boolean, onDayClick : (Int) -> Unit = {}) {
    // 현재 월의 날짜 수
    val yearMonth = java.time.YearMonth.of(year, month)
    val daysInMonth = yearMonth.lengthOfMonth() // 1월이면 31일
    // 이번달 1일 요일을 계산
    val startWeek = java.time.LocalDate.of(year, month, 1).dayOfWeek.value % 7

    // dailyAmountColor가 지출이면 true, 수입이면 false
    val amountColor = if (dailyAmountColor) Color.Red else Color.Blue

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach{
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // 총 필요한 칸수
        val totalCells = startWeek + daysInMonth
        val rows = (totalCells / 7) + 1
        var dayNumber = 1

        Column {
            repeat(rows) { rowIndex ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    repeat(7) { col ->
                        val cellIndex = col + (rowIndex * 7)
                        if (cellIndex < startWeek || dayNumber > daysInMonth) {
                            Box(modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                            )
                        }
                        else {
                            val thisDay = dayNumber
                            Column (
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clickable {
                                        onDayClick(thisDay)
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.height(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // 금액이 있으면 표시
                                    val amount = dailyAmount[thisDay]
                                    if (amount != null && amount != 0) {
                                        val textAmount = formatAmountShort(amount)
                                        Text(
                                            text = textAmount,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            color = amountColor,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // 날짜
                                Text(text = thisDay.toString(), fontSize = 20.sp)

                                dayNumber++

                            }
                        }
                    }
                }
            }
        }
    }
}

// 금액 변환
fun formatAmountShort(amount: Int): String {
    fun formatValue(value: Double): String {
        // 불필요한 소수 .0 제거
        val text = String.format(Locale.KOREA, "%.1f", value)
        return if (text.endsWith(".0")) text.dropLast(2) else text
    }

    return when {
        amount >= 100_000_000 -> { // 억 단위
            val v = amount / 100_000_000.0
            "${formatValue(v)}억"
        }
        amount >= 10_000 -> { // 만 단위
            val v = amount / 10_000.0
            "${formatValue(v)}만"
        }
        amount >= 1_000 -> { // 천 단위
            val v = amount / 1_000.0
            "${formatValue(v)}천"
        }
        else -> amount.toString()
    }
}
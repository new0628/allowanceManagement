package com.example.allowancemanagement.view.home

import android.util.Log

import androidx.compose.foundation.gestures.detectTapGestures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 수입 / 지출 거래 내용 UI
@Composable
fun ItemTitle() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
    ) {
        Text (
            text = "날짜",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text (
            text = "내용",
            modifier = Modifier.weight(3f),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text (
            text = "금액",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// 거래 내역 한줄 표시
@Composable
fun TranslateItemName (date : String, description : String, amount : Int, onClick : () -> Unit = {}, onLongPress : () -> Unit = {}) {
    val showDate = remember(date) { formatDateForUI(date) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onClick()
                        Log.d("Item클릭", "날짜 : $date 내용 : $description 금액 : $amount")
                    },
                    onLongPress = {
                        onLongPress()
                    }
                )
            }
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 8.dp),
        ) {
            // 날짜
            Text (
                text = showDate,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // 내용
            Text (
                text = description,
                modifier = Modifier
                    .weight(3f)
                    .padding(end = 10.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // 금액
            Text (
                text = amount.toString() + "원",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 10.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}

// 리스트에서 ex : 11-29 처럼 보이기 위함
fun formatDateForUI(date : String) : String {
    return try {
        val input = DateTimeFormatter.ISO_LOCAL_DATE
        val output = DateTimeFormatter.ofPattern("MM-dd")
        LocalDate.parse(date, input).format(output)
    } catch (e: Exception) {
        date
    }
}
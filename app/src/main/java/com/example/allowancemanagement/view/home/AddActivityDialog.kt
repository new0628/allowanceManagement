package com.example.allowancemanagement.view.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog

import android.util.Log

import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

// 내역 추가를 위한 다이얼로그
@Composable
fun AddActivityDialog (
    onDismiss : () -> Unit,
    onConfirm : (ActivityType, String, String, Int) -> Unit
) {
    // 기본적인 저장 변수들
    var selectedType by remember { mutableStateOf(ActivityType.EXPENSE) }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    // 날짜 달력 관련 변수
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    val dateFormat = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
    val dateText = selectedDate.format(dateFormat)
    val context = LocalContext.current
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }
    // ㅡㅡㅡㅡ 에러 관련 ㅡㅡㅡㅡ
    // 금액 입력 에러 체크
    var amountError by remember { mutableStateOf<String?>(null) }
    // 내용 빈칸
    var descriptionError by remember { mutableStateOf<String?>(null) }

    // 다이얼로그 흔들기용 애니매이션
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "내역 추가",
                    fontSize = 25.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.offset{
                    IntOffset(shakeOffset.value.roundToInt(), 0)
                }
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TypeToggleButton(
                        text = "지출",
                        isSelected = (selectedType == ActivityType.EXPENSE),
                        onClick = { selectedType = ActivityType.EXPENSE },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                    TypeToggleButton(
                        text = "수입",
                        isSelected = (selectedType == ActivityType.INCOME),
                        onClick = { selectedType = ActivityType.INCOME },
                        modifier = Modifier.weight(1f)
                    )

                }

                // 날짜 달력
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { },
                    label = { Text("날짜") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "날짜 선택",
                            modifier = Modifier.clickable {
                                datePickerDialog.show()
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                //  내용
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (descriptionError != null && it.isNotBlank()) {
                            descriptionError = null
                        } },
                    label = { Text("내용") },
                    isError = descriptionError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (descriptionError != null) {
                    Text(
                        text = descriptionError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 금액
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        if (amountError != null) {
                            amountError = null
                        } },
                    label = { Text("금액") },
                    isError = amountError != null,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                if (amountError != null) {
                    Text(
                        text = amountError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    var isValid = false

                    // 내용 빈칸 체크
                    if (description.isBlank()) {
                        descriptionError = "내용을 입력해주세요"
                        isValid = true
                    }

                    // 금액 유효성 체크
                    val amount = amountText.toIntOrNull()
                    if (amount == null) {
                        amountError = "금액은 숫자만 입력할 수 있습니다"
                        isValid = true
                    }
                    else if (amount <= 0) {
                        amountError = "금액은 0보다 커야 합니다"
                        isValid = true
                    }

                    if (isValid) {
                        scope.launch {
                            shakeOffset.snapTo(0f)
                            shakeOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = keyframes {
                                    durationMillis = 400
                                    (-12f) at 50
                                    12f at 100
                                    (-8f) at 150
                                    8f at 200
                                    (-4f) at 250
                                    4f at 300
                                    0f at 400
                                }
                            )
                        }
                        return@TextButton
                    }

                    val safeAmount = amount!!
                    val dateTranslate = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    Log.d("추가됨", "타입=${selectedType}, 날짜=${dateTranslate}, 내용=${description}, 금액=${amount}")
                    onConfirm(selectedType, dateTranslate, description, safeAmount)
                }
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("취소")
            }
        }
    )
}
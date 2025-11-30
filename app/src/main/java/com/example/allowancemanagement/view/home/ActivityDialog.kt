package com.example.allowancemanagement.view.home

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
import androidx.compose.ui.unit.IntOffset
import java.time.ZoneId
import kotlin.math.roundToInt

enum class DialogMode {
    ADD, EDIT
}
// 내역 추가를 위한 다이얼로그
@Composable
fun ActivityDialog (
    mode: DialogMode,
    initialType: ActivityType = ActivityType.EXPENSE,
    initialDate: LocalDate = LocalDate.now(),
    initialDescription: String = "",
    initialAmount: Int = 0,
    showTypeSelector: Boolean, // EDIT일 땐 false로 변경되는 변수
    onDismiss : () -> Unit,
    onConfirm : (ActivityType, String, String, Int) -> Unit
) {
    // 기본적인 저장 변수들
    var selectedType by remember(initialType) { mutableStateOf(initialType) }
    // 날짜
    var selectedDate by remember(initialDate) { mutableStateOf(initialDate) }
    // 내용
    var description by remember(initialDescription) { mutableStateOf(initialDescription) }
    // 금액 (0이면 빈 문자열로 시작하게 하고 싶으면 조건 줄 수도 있음)
    var amountText by remember(initialAmount) {
        mutableStateOf(
            if (initialAmount == 0) "" else initialAmount.toString()
        )
    }

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
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).apply {
            val minDate = LocalDate.of(2024, 1, 1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            datePicker.minDate = minDate
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

    // DialogMode에 따라 Text 수정
    val titleText = if (mode == DialogMode.ADD) "내역 추가" else "내역 수정"
    val conformText = if (mode == DialogMode.ADD) "추가" else "수정"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = titleText,
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
                // 타입 선택 토글 버튼
                if (showTypeSelector) {
                    Row(
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
                }

                // 날짜 입력
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

                //  내용 입력
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (descriptionError != null && it.isNotBlank()) {
                            descriptionError = null
                        } },
                    label = { Text("내용") },
                    isError = descriptionError != null,
                    singleLine = true,
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

                // 금액 입력
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        if (amountError != null) {
                            amountError = null
                        } },
                    label = { Text("금액") },
                    isError = amountError != null,
                    singleLine = true,
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
                    // 에러가 있으면 흔들기 애니메이션
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
                Text(conformText)
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
package com.example.allowancemanagement.view.home
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowancemanagement.viewModel.HomeViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


enum class ActivityType {
    EXPENSE, INCOME
}
@Composable
fun Home(viewModel : HomeViewModel, modifier: Modifier = Modifier) {
    // 상태
    val balance = viewModel.balance.collectAsState()
    val expenseListState = viewModel.expenseList.collectAsState()
    val incomeListState = viewModel.incomeList.collectAsState()

    val formatBalance = NumberFormat.getNumberInstance(Locale.KOREA).format(balance.value)
    // 지출 / 수입 탭 저장 변수 (기본값 지출)
    var selectedTab by remember { mutableStateOf(TabName.EXPENSE) }
    // 데이터 추가 버튼 flag
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ){
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "잔액",
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(start = 50.dp)
                        .weight((1f)),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "$formatBalance 원",
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(end = 40.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 서치바
            SearchBar()

            // 날짜 조정
            MonthSelector()

            // 수입 / 지출 탭
            TabBar(
                selectedTab = selectedTab,
                onTabSelected = { tab -> selectedTab = tab }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 선택된 지출 / 수입 탭 기준으로 리스트 불러오기
            when (selectedTab) {
                TabName.EXPENSE -> ActivityList(
                    items = expenseListState.value,
                    onDelete = { item -> viewModel.removeExpense(item) }
                )

                TabName.INCOME -> ActivityList(
                    items = incomeListState.value,
                    onDelete = { item -> viewModel.removeIncome(item) }
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "데이터 추가"
            )
        }
        if (showAddDialog) {
            AddActivityDialog(
                onDismiss = {showAddDialog = false},
                onConfirm = { activityType, date, description, amount ->
                    when (activityType) {
                        ActivityType.EXPENSE -> viewModel.addExpense(
                            date = date,
                            description = description,
                            amount = amount
                        )
                        ActivityType.INCOME -> viewModel.addIncome(
                            date = date,
                            description = description,
                            amount = amount
                        )
                    }
                    showAddDialog = false
                }
            )
        }
    }
}

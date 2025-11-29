package com.example.allowancemanagement.view.home
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowancemanagement.model.ActivityUI
import com.example.allowancemanagement.viewModel.HomeViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale


enum class ActivityType(val code : Int) {
    EXPENSE(0), INCOME(1)
}
@Composable
fun Home(viewModel : HomeViewModel, modifier: Modifier = Modifier) {
    // 상태
    val balance = viewModel.balance.collectAsState()
    val formatBalance = NumberFormat.getNumberInstance(Locale.KOREA).format(balance.value)

    // 지출 / 수입 탭 저장 변수 (기본값 지출)
    var selectedTab by remember { mutableStateOf(TabName.EXPENSE) }
    // 데이터 추가 버튼 flag
    var showAddDialog by remember { mutableStateOf(false) }

    // 수정 대상 아이템
    var editTarget by remember { mutableStateOf<ActivityUI?>(null) }

    // 검색어 필터링된 리스트 가져오기
    val searchQueryState = viewModel.searchQuery.collectAsState()
    val expenseListState = viewModel.expenseList.collectAsState()
    val incomeListState = viewModel.incomeList.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
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
            SearchBar(
                query = searchQueryState.value,
                onQueryChange = { q -> viewModel.updateSearchQuery(q) })

            // 날짜 조정
            MonthSelector(
                year = selectedYear,
                month = selectedMonth,
                onMonthChange = { y, m -> viewModel.updateSelectDate(y, m) })

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
                    onDelete = { item -> viewModel.removeExpense(item) },
                    onItemClick = { item -> editTarget = item }
                )

                TabName.INCOME -> ActivityList(
                    items = incomeListState.value,
                    onDelete = { item -> viewModel.removeIncome(item) },
                    onItemClick = { item -> editTarget = item }
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
            ActivityDialog(
                mode = DialogMode.ADD,
                initialType = ActivityType.EXPENSE,
                initialDate = LocalDate.now(),
                initialDescription = "",
                initialAmount = 0,
                showTypeSelector = true,
                onDismiss = { showAddDialog = false },
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

        val target = editTarget
        if (target != null) {

            ActivityDialog(
                mode = DialogMode.EDIT,
                initialType = if (target.type == 0) ActivityType.EXPENSE else ActivityType.INCOME,
                initialDate = LocalDate.parse(target.date),
                initialDescription = target.description,
                initialAmount = target.amount,
                showTypeSelector = false,   // 수입/지출은 수정X
                onDismiss = { editTarget = null },
                onConfirm = { _, date, description, amount ->
                    if (target.type == 0) {
                        viewModel.updateExpense(
                            original = target,
                            newDate = date,
                            newDescription = description,
                            newAmount = amount
                        )
                    } else {
                        viewModel.updateIncome(
                            original = target,
                            newDate = date,
                            newDescription = description,
                            newAmount = amount
                        )
                    }
                    editTarget = null
                }
            )

        }
    }
}

package com.example.allowancemanagement.view.monthStats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowancemanagement.view.home.ItemTitle
import com.example.allowancemanagement.view.home.MonthSelector
import com.example.allowancemanagement.view.home.TabBar
import com.example.allowancemanagement.view.home.TabName
import com.example.allowancemanagement.view.home.TranslateItemName
import com.example.allowancemanagement.viewModel.HomeViewModel
import java.time.LocalDate

@Composable
fun MonthStatsMain(viewModel : HomeViewModel, modifier: Modifier = Modifier) {

    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    var selectedTab by remember { mutableStateOf(TabName.EXPENSE) }
    // 선택된 날짜
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    val dayDetailState = viewModel.dayDetailList.collectAsState()

    val dailyAmount by viewModel.dailySumMap.collectAsState()

//    val expenseListState = viewModel.expenseList.collectAsState()
//    val incomeListState = viewModel.incomeList.collectAsState()
//
//    // 지출 / 수입에따라 금액 계산
//    val baseList = when (selectedTab) {
//        TabName.EXPENSE -> expenseListState.value
//        TabName.INCOME -> incomeListState.value
//    }
    // 날짜별 금액 카운트
//    val dailyAmount : Map<Int, Int> = remember (baseList, selectedYear, selectedMonth) {
//        val yearMonthTranslate = "%04d-%02d".format(selectedYear, selectedMonth)
//
//        baseList.filter { it.date.startsWith(yearMonthTranslate) }
//            .groupBy { (LocalDate.parse(it.date).dayOfMonth) }
//            .mapValues { (_, dayItems) ->
//                dayItems.sumOf { it.amount }
//            }
//    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 제목
        Text(
            text = "월 통계",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 월 변경
        MonthSelector(
            year = selectedYear,
            month = selectedMonth,
            onMonthChange = { y, m ->
                viewModel.updateSelectDate(y, m, selectedTab)
                selectedDay = null
                            },
        )

        // EXPESNSE / INCOME 탭
        TabBar(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
                selectedDay = null
                viewModel.loadDailySum(tab)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 달력 UI
        CalendarDisplay(
            year = selectedYear,
            month = selectedMonth,
            dailyAmount = dailyAmount,
            dailyAmountColor = (selectedTab == TabName.EXPENSE),
            onDayClick = { day ->
                selectedDay = day
                viewModel.loadDayDetail(selectedYear, selectedMonth, day, selectedTab)
            }
        )

        if (selectedDay != null) {
            Spacer(modifier = Modifier.height(5.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.padding(top = 5.dp))

                ItemTitle()

                Spacer(modifier = Modifier.padding(top = 5.dp))

                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    color = Color.LightGray
                )

                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp)
                ) {
                    items (
                        dayDetailState.value,
                        key = { it.id }
                    ) { item ->
                        TranslateItemName(
                            date = item.date,
                            description = item.description,
                            amount = item.amount
                        )
                    }
                }
            }
        }
    }
}
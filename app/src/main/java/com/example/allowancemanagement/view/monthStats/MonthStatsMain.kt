package com.example.allowancemanagement.view.monthStats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowancemanagement.view.home.MonthSelector
import com.example.allowancemanagement.view.home.TabBar
import com.example.allowancemanagement.view.home.TabName
import com.example.allowancemanagement.viewModel.HomeViewModel

@Composable
fun MonthStatsMain(viewModel : HomeViewModel, modifier: Modifier = Modifier) {

    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    var selectedTab by remember { mutableStateOf(TabName.EXPENSE) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
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

        MonthSelector(
            year = selectedYear,
            month = selectedMonth,
            onMonthChange = { y, m -> viewModel.updateSelectMonthDate(y, m) },
        )

        TabBar(
            selectedTab = selectedTab,
            onTabSelected = { tab -> selectedTab = tab }
        )


    }
}
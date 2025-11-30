package com.example.allowancemanagement.view.yearStats

import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowancemanagement.view.home.TabBar
import com.example.allowancemanagement.view.home.TabName

import com.example.allowancemanagement.viewModel.YearStatsViewModel


@Composable
fun YearStatsMain(viewModel : YearStatsViewModel, modifier: Modifier = Modifier) {

    var selectedTab by remember { mutableStateOf(TabName.EXPENSE) }

    // 선택된 년도
    val selectedYear by viewModel.selectedYear.collectAsState()
    // 월 합계 가져오기
    val monthSumMap by viewModel.monthSumMap.collectAsState()

    // 화면이 처음 열리거나 년/탭이 변경될때마다 계산
    LaunchedEffect(selectedYear, selectedTab) {
        viewModel.loadMonthSum(selectedYear, selectedTab)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 화면 제목
        Text(
            text = "연 통계",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            //modifier = Modifier.padding(bottom = 10.dp)
        )

        // 연도 선택 UI
        YearSelector(year = selectedYear, onYearChange = { newYear ->
            viewModel.updateYear(newYear)
        })

        // EXPESNSE / INCOME 탭
        TabBar(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
            }
        )

        Spacer(modifier = Modifier.height(3.dp))

        // 월별 금액 표시
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            YearMonthDisplay(year = selectedYear, monthSumMap = monthSumMap)
        }

    }
}


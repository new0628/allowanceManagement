package com.example.allowancemanagement.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.allowancemanagement.view.home.Home
import com.example.allowancemanagement.view.monthStats.MonthStatsMain
import com.example.allowancemanagement.view.yearStats.YearStatsMain
import com.example.allowancemanagement.viewModel.HomeViewModel
import com.example.allowancemanagement.viewModel.MonthStatsViewModel
import com.example.allowancemanagement.viewModel.YearStatsViewModel


enum class MainTab {
    HOME,
    MonthStats,
    YearStats
}

@Composable
fun MainView() {
    val homeVm: HomeViewModel = viewModel()
    val monthVm: MonthStatsViewModel = viewModel()
    val yearVm: YearStatsViewModel = viewModel()

    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    // 최초 1회만 실행
    LaunchedEffect(Unit) {
        homeVm.loadInitialData()
    }


    Scaffold (
        bottomBar = {
            BottomTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        when (selectedTab) {
            MainTab.HOME -> {
                Home(
                    viewModel = homeVm,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        //.navigationBarsPadding()
                )
            }
            MainTab.MonthStats -> {
                MonthStatsMain(
                    viewModel = monthVm,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        //.navigationBarsPadding()
                )
            }
            MainTab.YearStats -> {
                YearStatsMain(
                    viewModel = yearVm,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        //.navigationBarsPadding()
                )
            }
        }
    }
}

@Composable
fun BottomTabBar (selectedTab : MainTab, onTabSelected : (MainTab) -> Unit, modifier: Modifier = Modifier) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(Color.DarkGray),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomTabItem(
            text = "HOME",
            isSelected = selectedTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            modifier = Modifier.weight(1f)
        )

        BottomTabItem(
            text = "월 통계",
            isSelected = selectedTab == MainTab.MonthStats,
            onClick = { onTabSelected(MainTab.MonthStats) },
            modifier = Modifier.weight(1f)
        )

        BottomTabItem(
            text = "연 통계",
            isSelected = selectedTab == MainTab.YearStats,
            onClick = { onTabSelected(MainTab.YearStats) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BottomTabItem (text : String, isSelected : Boolean, onClick : () -> Unit, modifier: Modifier = Modifier) {
    val textColor = if (isSelected) Color.White else Color.LightGray

    Box (
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 14.sp)
    }
}
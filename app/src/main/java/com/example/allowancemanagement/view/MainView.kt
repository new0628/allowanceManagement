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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allowancemanagement.view.home.Home
import com.example.allowancemanagement.view.monthStats.MonthStatsMain
import com.example.allowancemanagement.viewModel.HomeViewModel

enum class MainTab {
    HOME,
    Stats,
    Summary
}

@Composable
fun MainView(homeViewModel: HomeViewModel) {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    Scaffold (
        bottomBar = {
            BottomTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        //val layoutDirection = LocalLayoutDirection.current
        when (selectedTab) {
            MainTab.HOME -> {
                Home(
                    viewModel = homeViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
//                        .padding(
//                            start = innerPadding.calculateStartPadding(layoutDirection),
//                            top = innerPadding.calculateTopPadding(),
//                            end = innerPadding.calculateEndPadding(layoutDirection),
//                            bottom = 20.dp
//                        )
                )
            }
            MainTab.Stats -> {
                MonthStatsMain(
                    viewModel = homeViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            MainTab.Summary -> {
                SummaryView()
            }
        }
    }
}

@Composable
fun BottomTabBar (selectedTab : MainTab, onTabSelected : (MainTab) -> Unit) {
    Row (
        modifier = Modifier
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
            text = "통계",
            isSelected = selectedTab == MainTab.Stats,
            onClick = { onTabSelected(MainTab.Stats) },
            modifier = Modifier.weight(1f)
        )

        BottomTabItem(
            text = "요약",
            isSelected = selectedTab == MainTab.Summary,
            onClick = { onTabSelected(MainTab.Summary) },
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
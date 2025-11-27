package com.example.allowancemanagement.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 수입 지출 탭 전환 UI
@Composable
fun TabBar(selectedTab : TabName, onTabSelected : (TabName) -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TabButton(
            text = "지출",
            isSelected = selectedTab == TabName.EXPENSE,
            onClick = { onTabSelected(TabName.EXPENSE)},
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = "수입",
            isSelected = selectedTab == TabName.INCOME,
            onClick = {onTabSelected(TabName.INCOME)},
            modifier = Modifier.weight(1f)
        )

    }
}
@Composable
fun TabButton (text: String, isSelected : Boolean, onClick : () -> Unit, modifier : Modifier = Modifier) {
    val bgColor = if (isSelected) Color.Red else Color.LightGray
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Box (
        modifier = modifier
            .height(40.dp)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp
        )
    }
}
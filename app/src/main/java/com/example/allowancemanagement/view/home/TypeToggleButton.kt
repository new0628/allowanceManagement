package com.example.allowancemanagement.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TypeToggleButton (text : String, isSelected : Boolean, onClick : () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (isSelected) Color.Red else Color.LightGray
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Box(
        modifier = modifier
            .height(36.dp)
            .background(bgColor, shape = MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ){
        Text(text = text, color = textColor, fontSize = 14.sp)
    }
}
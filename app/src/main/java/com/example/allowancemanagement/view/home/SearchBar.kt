package com.example.allowancemanagement.view.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 서치바 기능
@Composable
fun SearchBar(query : String, onQueryChange : (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(top = 15.dp)
    ) {
        TextField(
            value = query,
            onValueChange = { newText ->
                onQueryChange(newText)
            },
            placeholder = {
                Text("검색")
            },
            leadingIcon = {
                Icon (
                    imageVector = Icons.Default.Search,
                    contentDescription = "search"
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors()
        )
    }
}
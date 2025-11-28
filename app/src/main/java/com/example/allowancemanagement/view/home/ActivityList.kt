package com.example.allowancemanagement.view.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import com.example.allowancemanagement.model.ActivityUI

// 수입 / 지출 리스트 UI

@Composable
fun ActivityList (
    items : List<ActivityUI>,
    onDelete : (ActivityUI) -> Unit,
    onItemClick : (ActivityUI) ->Unit
) {

    // 다이얼로그 flag
    var showDeleteDialog by remember { mutableStateOf(false) }
    // 선택된 아이템 담는 변수
    var selectedItem by remember { mutableStateOf<ActivityUI?>(null) }

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
            .padding(top = 8.dp)
    ) {
        items(
            items,
            key = { it.id }
        ) { item ->
            TranslateItemName(
                date = item.date,
                description = item.description,
                amount = item.amount,
                onClick = {
                    onItemClick(item)
                },
                onLongPress = {
                    selectedItem = item
                    showDeleteDialog = true
                }
            )
        }
    }

    // 삭제 여부 다이얼로그
    if (showDeleteDialog && selectedItem != null) {
        AlertDialog(
            title = {
                Text("삭제")
            },

            text = {
                Text(
                    text =  "${selectedItem!!.date}  ${selectedItem!!.description}  ${selectedItem!!.amount}원을\n삭제하시겠습니까?"
                )
            },

            onDismissRequest = {
                showDeleteDialog = false
            },

            confirmButton = {
                Text(
                    text = "삭제",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onDelete(selectedItem!!)
                            showDeleteDialog = false
                        }
                )
            },

            dismissButton = {
                Text(
                    text = "취소",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            showDeleteDialog = false
                        }
                )
            }
        )
    }
}

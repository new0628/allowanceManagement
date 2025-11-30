package com.example.allowancemanagement.model

data class ActivityUI (
    val id : Int,
    val type : Int, // 0이면 지출, 1이면 수입
    val date : String,
    val description : String,
    val amount : Int
)
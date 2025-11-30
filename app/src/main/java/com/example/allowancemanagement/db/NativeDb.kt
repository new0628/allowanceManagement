package com.example.allowancemanagement.db

import com.example.allowancemanagement.model.ActivityUI

object NativeDb {
    init {
        System.loadLibrary("native_db")
    }

    //디버깅용
    external fun debugPrintAll()

    external fun open(path: String) // db열기
    external fun close() // db 닫기

    // 잔액 조회
    external fun getBalance() : Int

    // INSERT 함수
    external fun insertActivity (
        type: Int,
        date: String,
        description: String,
        amount: Int
    ) : Long

    // UPDATE 함수
    external fun updateActivity(
        id : Int,
        date : String,
        description : String,
        amount: Int
    )

    // SELECT 함수 : 월 전체 + 검색어
    external fun loadByMonthAndQuery(
        year : Int,
        month : Int,
        query : String
    ) : List<ActivityUI>

    // SELECT 함수 : 특정 날짜 + 타입
    external fun loadByDateAndType(
        year : Int,
        month : Int,
        day : Int,
        type : Int
    ) : List<ActivityUI>

    // SELECT 함수 : 일별 합계
    external fun getDailySum(year : Int, month : Int, type : Int) : IntArray
    // SELECT 함수 : 월별 합계
    external fun getMonthSum(year : Int, type : Int) : IntArray

    // DELETE 함수
    external fun deleteActivity(id : Int)


}
package com.example.allowancemanagement.db

import com.example.allowancemanagement.model.ActivityUI

object NativeDb {
    init {
        System.loadLibrary("native_db")
    }

    //디버깅용
    external fun debugPrintAll()

    external fun open(path: String)
    external fun close()

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

    // SELECT 함수
    external fun loadByMonthAndQuery(
        year : Int,
        month : Int,
        query : String
    ) : List<ActivityUI>

    // DELETE 함수
    external fun deleteActivity(id : Int)
    // SELECT 함수
    //external fun loadAll() : Array<ActivityUI>
}
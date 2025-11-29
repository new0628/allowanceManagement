package com.example.allowancemanagement.model

import com.example.allowancemanagement.db.NativeDb
import com.example.allowancemanagement.db.NativeDb.getDailySum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeRepository {
    // ───────── 지출 / 수입 리스트 ─────────
    private val _expenseList = MutableStateFlow<List<ActivityUI>>(emptyList())
    val expenseList : StateFlow<List<ActivityUI>> = _expenseList.asStateFlow()

    private val _incomeList = MutableStateFlow<List<ActivityUI>>(emptyList())
    val incomeList : StateFlow<List<ActivityUI>> = _incomeList.asStateFlow()

    // 특정 날짜 내역
    private val _dayDetailList = MutableStateFlow<List<ActivityUI>>(emptyList())
    val dayDatailList : StateFlow<List<ActivityUI>> = _dayDetailList.asStateFlow()

    // 날짜별 합계
    val dailySumMap = MutableStateFlow<Map<Int, Int>>(emptyMap())

    init {}

    // 정렬 기준 1) 날짜, 2) 들어온순서
    private val activityComparator = compareBy<ActivityUI> (
        { it.date },
        { it.id }
    )

    // ─────────────── 잔액 조회 ───────────────
    fun loadBalance() : Int {
        return NativeDb.getBalance()
    }

    // ─────────────── 추가 ───────────────
    fun addExpense(date : String, description : String, amount : Int) {
        NativeDb.insertActivity(type = 0, date = date, description = description, amount = amount)
    }

    fun addIncome(date : String, description : String, amount : Int) {
        NativeDb.insertActivity(type = 1, date = date, description = description, amount = amount)
    }

    // ─────────────── 수정 ───────────────
    fun updateExpense (original : ActivityUI, newDate : String, newDescription : String, newAmount : Int) {
        NativeDb.updateActivity(id = original.id, date = newDate, description = newDescription, amount = newAmount)
    }

    fun updateIncome(original: ActivityUI, newDate: String, newDescription: String, newAmount: Int) {
        NativeDb.updateActivity(id = original.id, date = newDate, description = newDescription, amount = newAmount)
    }

    // ─────────────── 삭제 ───────────────
    fun removeExpense(item : ActivityUI) {
        NativeDb.deleteActivity(item.id)
    }

    fun removeIncome(item : ActivityUI) {
        NativeDb.deleteActivity(item.id)
    }

    // ─────────────── 불러오기 ───────────────
    // 월 전체 내역 조회
    fun reloadFromDb (year : Int, month : Int, query : String) {
        val uiList : List<ActivityUI> = NativeDb.loadByMonthAndQuery(year, month, query).sortedWith(activityComparator)

        _expenseList.value = uiList.filter { it.type == 0 }
        _incomeList.value = uiList.filter { it.type == 1 }

    }

    // 특정 날짜 조회
    fun loadDayDetail(year : Int, month : Int, day : Int, type : Int) {
        val uiList = NativeDb.loadByDateAndType(year, month, day, type)
        _dayDetailList.value = uiList
    }

    // 날짜별 합계 조회
    fun loadDailySum(year : Int, month : Int, type : Int) {
        val raw = NativeDb.getDailySum(year, month, type) ?: return

        val map = mutableMapOf<Int, Int>()
        var i = 0
        while (i+1 < raw.size) {
            val day = raw[i]
            val sum = raw[i+1]
            map[day] = sum
            i += 2
        }
        dailySumMap.value = map
    }
}
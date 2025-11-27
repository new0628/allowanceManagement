package com.example.allowancemanagement.viewModel

import androidx.lifecycle.ViewModel
import com.example.allowancemanagement.view.home.ActivityUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    // ───────── 잔액 ─────────
    private val _balance = MutableStateFlow(10000)
    val balance = _balance

    // 잔액 업데이트
    fun updateBalance(newValue : Int) {
        _balance.value = newValue
    }

    // ───────── 지출 / 수입 리스트 ─────────
    private val _expenseList = MutableStateFlow<List<ActivityUI>>(emptyList())
    val expenseList : StateFlow<List<ActivityUI>> = _expenseList.asStateFlow()

    private val _incomeList = MutableStateFlow<List<ActivityUI>>(emptyList())
    val incomeList : StateFlow<List<ActivityUI>> = _incomeList.asStateFlow()

    init {
        _expenseList.value = List(15) { index ->
            val day = index + 1
            ActivityUI(
                id = index,
                date = "2025-11-%02d".format(day),
                description = "스타벅스 $index",
                amount = 10_000 * (index + 1)
            )
        }

        _incomeList.value = List(10) { index ->
            val day = index + 1
            ActivityUI(
                id = index,
                date = "2025-11-%02d".format(day),
                description = "용돈 $index",
                amount = 5_000 * (index + 1)
            )
        }
    }

    // 정렬 기준 1) 날짜, 2) 들어온순서
    private val activityComparator = compareBy<ActivityUI> (
        { it.date },
        { it.id }
    )

    // ㅡㅡㅡㅡ 추가 ㅡㅡㅡㅡ
    fun addExpense(date : String, description : String, amount : Int) {
        val current = _expenseList.value
        val newID = (current.maxOfOrNull { it.id } ?: 0) + 1

        val newItem = ActivityUI (
            id = newID,
            date = date,
            description = description,
            amount = amount
        )

        val updateList = (current + newItem).sortedWith(activityComparator)
        _expenseList.value = updateList.sortedWith(activityComparator)
        _balance.value -= amount
    }

    fun addIncome(date : String, description : String, amount : Int) {
        val current = _incomeList.value
        val newId = (current.maxOfOrNull { it.id } ?: 0) + 1

        val newItem = ActivityUI(
            id = newId,
            date = date,
            description = description,
            amount = amount
        )

        val updateList = (current + newItem).sortedWith(activityComparator)
        _incomeList.value = updateList

        _balance.value += amount
    }

    // ㅡㅡㅡㅡ 삭제 ㅡㅡㅡㅡ
    fun removeExpense(item : ActivityUI) {
        _expenseList.value = _expenseList.value.filterNot { it.id == item.id }.sortedWith(activityComparator)
        // 나중에 잔액 조정해야함
    }

    fun removeIncome(item : ActivityUI) {
        _incomeList.value = _incomeList.value.filterNot { it.id == item.id }.sortedWith(activityComparator)
        // 나중에 잔액 조정해야함
    }

}
package com.example.allowancemanagement.viewModel

import androidx.lifecycle.ViewModel
import com.example.allowancemanagement.model.HomeRepository
import com.example.allowancemanagement.model.ActivityUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class HomeViewModel() : ViewModel() {

    private val repo = HomeRepository()
    // ───────── 잔액 ─────────
    private val _balance = MutableStateFlow(0)
    val balance = _balance

    // 검색어
    private val _searchQuery = MutableStateFlow("")
    val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    // 선택된 년/월
    private val today = LocalDate.now()
    private val _selectedYear = MutableStateFlow(today.year)
    private val _selectedMonth = MutableStateFlow(today.monthValue)
    val selectedYear : StateFlow<Int> = _selectedYear.asStateFlow()
    val selectedMonth : StateFlow<Int> = _selectedMonth.asStateFlow()

    // 기존에 있던 리스트 repo에서 불러오기
    val expenseList : StateFlow<List<ActivityUI>> = repo.expenseList
    val incomeList : StateFlow<List<ActivityUI>> = repo.incomeList

//    private val activityComparator = compareBy<ActivityUI>(
//        { it.date },
//        { it.id }
//    )

    // ─────────────── 아래부터 함수 ───────────────
    private fun reloadFromDb() {
        val year = _selectedYear.value
        val month = _selectedMonth.value
        val query = _searchQuery.value
        repo.reloadFromDb(year, month, query)
    }

    fun loadInitialData() {
        reloadFromDb()
        updateBalance()
    }

    // 잔액 업데이트
    fun updateBalance() {
        _balance.value = repo.loadBalance()
    }

    // 검색어 업데이트 + 선택된 년/월
    fun updateSearchQuery(newQuery : String) {
        _searchQuery.value = newQuery
        reloadFromDb()
    }

    fun updateSelectDate(year : Int, month : Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
        reloadFromDb()
    }

    fun addExpense(date : String, description : String, amount : Int) {
        repo.addExpense(date, description, amount)
        reloadFromDb()
        updateBalance()
    }

    fun addIncome(date : String, description : String, amount : Int) {
        repo.addIncome(date, description, amount)
        reloadFromDb()
        updateBalance()
    }

    fun updateExpense (original : ActivityUI, newDate : String, newDescription : String, newAmount : Int
    ) {
        repo.updateExpense(original, newDate, newDescription, newAmount)
        reloadFromDb()
        updateBalance()
    }

    fun updateIncome(original: ActivityUI, newDate: String, newDescription: String, newAmount: Int) {
        repo.updateIncome(original, newDate, newDescription, newAmount)
        reloadFromDb()
        updateBalance()
    }

    fun removeExpense(item : ActivityUI) {
        repo.removeExpense(item)
        reloadFromDb()
        updateBalance()
    }

    fun removeIncome(item : ActivityUI) {
        repo.removeIncome(item)
        reloadFromDb()
        updateBalance()
    }

}
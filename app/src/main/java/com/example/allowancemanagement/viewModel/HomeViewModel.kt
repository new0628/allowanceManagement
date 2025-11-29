package com.example.allowancemanagement.viewModel

import androidx.lifecycle.ViewModel
import com.example.allowancemanagement.model.HomeRepository
import com.example.allowancemanagement.model.ActivityUI
import com.example.allowancemanagement.view.home.TabName
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

    // 선택된 날짜 내역
    val dayDetailList : StateFlow<List<ActivityUI>> = repo.dayDatailList
    // 날짜뱔 합계
    val dailySumMap = repo.dailySumMap

    // ─────────────── 아래부터 함수 ───────────────

    fun loadInitialData() {
        reloadFromDb()
        updateBalance()
    }

    private fun reloadFromDb() {
        val year = _selectedYear.value
        val month = _selectedMonth.value
        val query = _searchQuery.value
        repo.reloadFromDb(year, month, query)
    }

    fun loadDailySum(tab : TabName) {
        val type = if (tab == TabName.EXPENSE) 0 else 1
        repo.loadDailySum(year = _selectedYear.value, month = _selectedMonth.value, type = type)
    }

    fun loadDayDetail(year : Int, month : Int, day : Int, tab : TabName) {
        val type = when(tab) {
            TabName.EXPENSE -> 0
            TabName.INCOME -> 1
        }
        repo.loadDayDetail(year, month, day, type)
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

    //
    fun updateSelectDate(year : Int, month : Int, tab : TabName) {
        _selectedYear.value = year
        _selectedMonth.value = month
        reloadFromDb()
        loadDailySum(tab)
    }

    fun addExpense(date : String, description : String, amount : Int) {
        repo.addExpense(date, description, amount)
        reloadFromDb()
        updateBalance()
        loadDailySum(TabName.EXPENSE)
    }

    fun addIncome(date : String, description : String, amount : Int) {
        repo.addIncome(date, description, amount)
        reloadFromDb()
        updateBalance()
        loadDailySum(TabName.INCOME)
    }

    fun updateExpense (original : ActivityUI, newDate : String, newDescription : String, newAmount : Int
    ) {
        repo.updateExpense(original, newDate, newDescription, newAmount)
        reloadFromDb()
        updateBalance()
        loadDailySum(TabName.EXPENSE)
    }

    fun updateIncome(original: ActivityUI, newDate: String, newDescription: String, newAmount: Int) {
        repo.updateIncome(original, newDate, newDescription, newAmount)
        reloadFromDb()
        updateBalance()
        loadDailySum(TabName.INCOME)
    }

    fun removeExpense(item : ActivityUI) {
        repo.removeExpense(item)
        reloadFromDb()
        updateBalance()
        loadDailySum(TabName.EXPENSE)
    }

    fun removeIncome(item : ActivityUI) {
        repo.removeIncome(item)
        reloadFromDb()
        updateBalance()
        loadDailySum(TabName.INCOME)
    }

}
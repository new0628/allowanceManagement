package com.example.allowancemanagement.viewModel

import androidx.lifecycle.ViewModel
import com.example.allowancemanagement.model.ActivityUI
import com.example.allowancemanagement.model.HomeRepository
import com.example.allowancemanagement.view.home.TabName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class MonthStatsViewModel : ViewModel() {
    private val monthRepo = HomeRepository()

    // 선택된 년/월
    private val today = LocalDate.now()
    private val _selectedYear = MutableStateFlow(today.year)
    private val _selectedMonth = MutableStateFlow(today.monthValue)
    val selectedYear : StateFlow<Int> = _selectedYear.asStateFlow()
    val selectedMonth : StateFlow<Int> = _selectedMonth.asStateFlow()

    // 선택된 날짜 내역
    val dayDetailList : StateFlow<List<ActivityUI>> = monthRepo.dayDatailList
    // 날짜별 (일, 달) 합계
    val dailySumMap = monthRepo.dailySumMap

    // ───────── 함수들 ─────────
    fun updateSelectDate(year : Int, month : Int) { // tab : TabName
        _selectedYear.value = year
        _selectedMonth.value = month
        //loadDailySum(tab)
    }

    fun loadDailySum(tab : TabName) {
        val type = if (tab == TabName.EXPENSE) 0 else 1
        monthRepo.loadDailySum(year = _selectedYear.value, month = _selectedMonth.value, type = type)
    }

    fun loadDayDetail(year : Int, month : Int, day : Int, tab : TabName) {
        val type = when(tab) {
            TabName.EXPENSE -> 0
            TabName.INCOME -> 1
        }
        monthRepo.loadDayDetail(year, month, day, type)
    }
}
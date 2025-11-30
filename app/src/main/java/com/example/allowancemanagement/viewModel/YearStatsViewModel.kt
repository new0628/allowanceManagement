package com.example.allowancemanagement.viewModel

import androidx.lifecycle.ViewModel
import com.example.allowancemanagement.model.HomeRepository
import com.example.allowancemanagement.view.home.TabName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class YearStatsViewModel : ViewModel() {
    private val yearRepo = HomeRepository()

    // 선택된 연도
    private val todayYear = LocalDate.now().year
    private val _selectedYear = MutableStateFlow(todayYear)
    val selectedYear : StateFlow<Int> = _selectedYear.asStateFlow()
    // 날짜별 (달) 합계
    val monthSumMap = yearRepo.monthSumMap

    // ───────── 함수들 ─────────
    // 선택된 연도 업데이트
    fun updateYear(year : Int) {
        _selectedYear.value = year
    }

    // 월별 합계 로드 (지출/수입)
    fun loadMonthSum(year : Int, tab : TabName) {
        val type = when (tab) {
            TabName.EXPENSE -> 0
            TabName.INCOME -> 1
        }
        yearRepo.loadMonthSum(year, type)
    }
}
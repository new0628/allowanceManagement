package com.example.allowancemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview

import com.example.allowancemanagement.ui.theme.AllowanceManagementTheme
import com.example.allowancemanagement.viewModel.HomeViewModel
import androidx.activity.viewModels
import com.example.allowancemanagement.db.NativeDb
import com.example.allowancemanagement.model.HomeRepository
import com.example.allowancemanagement.view.MainView

class MainActivity : ComponentActivity() {
    private val homeVm : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = getDatabasePath("allowance.db")
        NativeDb.open(db.absolutePath)
        NativeDb.debugPrintAll()

        homeVm.loadInitialData()
        setContent {
            AllowanceManagementTheme {
                MainView(homeViewModel = homeVm)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NativeDb.close()
    }
}
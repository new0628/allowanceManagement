package com.example.allowancemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview

import com.example.allowancemanagement.ui.theme.AllowanceManagementTheme
import com.example.allowancemanagement.viewModel.HomeViewModel
import androidx.activity.viewModels
import com.example.allowancemanagement.view.MainView

class MainActivity : ComponentActivity() {
    private val homeVm : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AllowanceManagementTheme {
                MainView(homeViewModel = homeVm)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    AllowanceManagementTheme {
        val homeVm = HomeViewModel()
        MainView(homeViewModel = homeVm)
    }
}
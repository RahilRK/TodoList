package com.rk.todo.fragments.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rk.todo.Repository

class homeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return homeViewModel(repository) as T
    }
}
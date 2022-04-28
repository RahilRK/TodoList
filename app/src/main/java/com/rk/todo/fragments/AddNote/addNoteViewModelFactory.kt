package com.rk.todo.fragments.AddNote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rk.todo.Repository

class addNoteViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return addNoteViewModel(repository) as T
    }
}
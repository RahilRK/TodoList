package com.rk.todo.fragments.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rk.todo.Repository
import com.rk.todo.NotesModel

class homeViewModel(private val repository: Repository) : ViewModel() {

    val noteList: LiveData<List<NotesModel>> = repository.noteList

    suspend fun addNote(model: NotesModel) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.addNote(model)
//        }

        repository.addNote(model)
    }
}
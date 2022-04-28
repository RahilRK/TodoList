package com.rk.todo.fragments.AddNote

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rk.todo.Repository
import com.rk.todo.NotesModel

class addNoteViewModel(private val repository: Repository) : ViewModel() {

    var TAG = "addNoteViewModel"

    fun getNoteDetail(id : Int): LiveData<NotesModel> {
        return repository.getNoteDetail(id)
    }

    suspend fun addNote(model: NotesModel) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.addNote(model)
//        }

        repository.addNote(model)
    }

    suspend fun deleteNote(id: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteNote(id)
//        }

        repository.deleteNote(id)
    }

    suspend fun checkNoteExist(model: NotesModel): Int {
        return repository.checkNoteExist(model)
    }
}
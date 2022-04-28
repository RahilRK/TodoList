package com.rk.todo

import androidx.lifecycle.LiveData

class Repository(private val database: Database) {

    var TAG = "Repository"

    suspend fun checkNoteExist(model: NotesModel):Int {
        return database.notesDAO().checkNoteExist(model.id)
    }

    suspend fun addNote(model: NotesModel) {
        database.notesDAO().addNote(model)
    }

    suspend fun deleteNote(id: Int) {
        database.notesDAO().deleteNote(id)
    }

    val noteList: LiveData<List<NotesModel>>
        get() = database.notesDAO().getNoteList()

    fun getNoteDetail(id : Int):LiveData<NotesModel> {
        return database.notesDAO().getNoteDetail(id)
    }
}
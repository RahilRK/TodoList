package com.rk.todo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(model: NotesModel)

    @Query("DELETE FROM notes where id = :id")
    suspend fun deleteNote(id: Int)

    @Query("select * from notes where isRemoved = 0")
    fun getNoteList() :LiveData<List<NotesModel>>

    @Query("select * from notes where id = :id")
    fun getNoteDetail(id: Int) :LiveData<NotesModel>

    @Query("select count(notes.id) as id from notes where id =:id")
    suspend fun checkNoteExist(id: Int) :Int
}
package com.rk.todo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class NotesModel(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    @ColumnInfo(name = "title", defaultValue = "") val title:String,
    @ColumnInfo(name = "description",  defaultValue = "") val description:String,
    @ColumnInfo(name = "isRemoved",  defaultValue = "0") val isRemoved:Int = 0,
): Parcelable


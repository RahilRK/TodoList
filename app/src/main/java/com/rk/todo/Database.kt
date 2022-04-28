package com.rk.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [NotesModel::class], version = 2)
abstract class Database : RoomDatabase() {

    abstract fun notesDAO(): NotesDAO

    companion object {

        @Volatile
        private var INSTANCE: com.rk.todo.Database? = null

        fun getInstance(context: Context): com.rk.todo.Database {

            if (INSTANCE == null) {

                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        com.rk.todo.Database::class.java,
                        "notesDB"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                }
            }

            return INSTANCE!!
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes ADD COLUMN isRemoved INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }

}
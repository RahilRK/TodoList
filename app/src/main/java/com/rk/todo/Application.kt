package com.rk.todo

import android.app.Application

class Application: Application() {

    lateinit var globalClass: GlobalClass
    lateinit var database: Database
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()

        init()
    }

    fun init() {
        globalClass = GlobalClass.getInstance(applicationContext)
        database = Database.getInstance(applicationContext)
        repository = Repository(database)
    }
}
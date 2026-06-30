package com.example.final_project

import android.app.Application
import com.example.final_project.data.remote.RetrofitClient

class TourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}

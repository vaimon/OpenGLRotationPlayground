package ru.mmcs.openglrotationplayground

import android.app.Application
import android.content.Context

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = getApplicationContext()
    }

    companion object {
        private var context: Context? = null
        val appContext: Context?
            get() = context
    }
}
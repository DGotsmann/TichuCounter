package com.example.tichucounter0  // Make sure the package name matches your app's package

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize ThreeTenABP here
        AndroidThreeTen.init(this)
    }
}

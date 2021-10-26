package com.example.meethere.sharedpreferences

import android.app.Application

//SharedPreferences 클래스는 앱에 있는 다른 액티비티보다 먼저 생성되어야 다른 곳에 데이터를 넘겨줄 수 있다.
class App : Application() {

    companion object {
        lateinit var prefs: PreferenceManager
    }
    //prefs라는 이름의 PreferenceManager 하나만 생성할 수 있도록 설정

    override fun onCreate() {
        prefs = PreferenceManager(applicationContext)
        super.onCreate()
    }
}
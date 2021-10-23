package com.example.meethere.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    val PREFS_FILENAME = "prefs"
    val PREF_KEY_TOKEN = "token"
    val PREF_MEMBERID = "memberId"
    val PREF_EMAIL = "email"
    val PREF_NAME = "name"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    //파일 이름과 token을 저장할 key 값을 만들고 prefs 인스턴스 초기화

    var token: String?
        get() = prefs.getString(PREF_KEY_TOKEN, "")
        set(value) = prefs.edit().putString(PREF_KEY_TOKEN, value).apply()
    //get,set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
    //set(value) 실행 시, value로 값을 대체한 후 저장

    var memberId: Long?
        get() = prefs.getLong(PREF_MEMBERID, 0)
        set(value) = prefs.edit().putLong(PREF_MEMBERID, value!!).apply()


    var email: String?
        get() = prefs.getString(PREF_EMAIL, "")
        set(value) = prefs.edit().putString(PREF_EMAIL, value).apply()

    var username: String?
        get() = prefs.getString(PREF_NAME, "")
        set(value) = prefs.edit().putString(PREF_NAME, value).apply()
}
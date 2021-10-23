package com.example.meethere.utils

object Constants {
    const val TAG: String = "로그"
}

enum class RESPONSE_STATE {
    OKAY,
    FAIL
}

object API {
    const val BASE_URL: String = "http://13.124.215.113:8080/"

    const val LOGIN: String = "authenticate"

    const val BOOKMARK_LIST = "api/bookmark/list"

    const val REGISTER = "api/members"
}

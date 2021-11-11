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

    const val REGISTER = "api/members"

    const val VERIFY = "api/members/verify"

    const val UPDATE = "api/members/update"

    const val FIND_ID = "api/members/findId"

    const val FIND_PW = "api/members/findPw"

    const val USER_INFO = "api/members/find"

    const val DELETE_MEM = "api/members/delete"

    const val SAVE_BOOKMARK = "api/bookmark/save"

    const val FIND_START_ADDRESS_LIST = "api/bookmark/load"

    const val BOOKMARK_LIST = "api/bookmark/list"

    const val UPDATE_BOOKMARK = "api/bookmark/update"

    const val DELETE_BOOKMARK = "api/bookmark/delete"

    const val SAVE_SHARE = "api/share/save"

    const val SHARE_DESTINATION = "api/share/destination"

    const val SHARE_START = "api/share/start"

    const val FIND_FRIEND = "api/friend/find"

    const val ADD_FRIEND = "api/friend/save"

    const val FRIEND_LIST = "api/friend/list"

    const val DELETE_FRIEND = "api/friend/delete"

}

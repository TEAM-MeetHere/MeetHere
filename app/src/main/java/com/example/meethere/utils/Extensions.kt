package com.example.meethere.utils

// 문자열이 제이슨 형태인지, 제이슨 배열 형태인지
fun String?.isJsonObejct():Boolean {
    if(this?.startsWith("{") == true && this.endsWith("}")){
        return true
    }else{
        return false
    }
//    return this?.startsWith("{") == true && this.endsWith("}")
}

//문자열이 제이슨 배열인지
fun String?.isJsonArray():Boolean{
    if(this?.startsWith("[") == true && this.endsWith("]")){
        return true
    }else{
        return false
    }
}
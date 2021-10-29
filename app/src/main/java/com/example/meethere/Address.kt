package com.example.meethere

import java.io.Serializable

data class Address (
    val address: String,
    val name: String
) : Serializable
package com.selcannarin.schoolbustrackerdriver.data.model

data class Driver(

    val email: String = "",

    val fullName: String = "",

    val licensePlate: String = "",

    val students: List<Int>? = null
)


package com.selcannarin.schoolbustrackerdriver.data.model

data class Student(

    var student_number : Int = 0,

    val student_name : String = "",

    val student_status: Boolean = true,

    val parent_phone_number: Long = 0,

    val student_adress: String = ""
)

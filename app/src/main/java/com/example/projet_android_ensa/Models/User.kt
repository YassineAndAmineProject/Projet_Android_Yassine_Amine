package com.example.projet_android_ensa.Models

data class User(
    var uuid:String,
    val email: String,
    var fullname:String,
    var image:String?
) {
    constructor(): this("","","","")
}

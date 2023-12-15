package com.example.projet_android_ensa.Models

data class Friend(
    var uuid:String,
    val name:String,
    val lastMsg:String,
    val image:String,
    val timeStamp: Long
) {
    constructor() : this("","","","",0)
}

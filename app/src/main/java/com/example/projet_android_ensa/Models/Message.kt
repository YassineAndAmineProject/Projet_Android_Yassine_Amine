package com.example.projet_android_ensa.Models

data class Message(val sender : String,
              val receiver : String,
              val text : String,
              val timeStamp: Long,
              var isReceived : Boolean = true
) {
    constructor(): this("","","",0)
}
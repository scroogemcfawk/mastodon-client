package com.github.scroogemcfawk.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface IDebuggable {

    val debug: Boolean
    val timer: Boolean
    fun deb(message: String) {
        val time = if (timer) "[${LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) }] "
                   else ""
        if (debug) println("$time$message")
    }
}

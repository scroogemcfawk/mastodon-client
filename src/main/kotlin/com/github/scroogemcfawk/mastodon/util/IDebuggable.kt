package com.github.scroogemcfawk.mastodon.util

interface IDebuggable {

    val debug: Boolean
    fun deb(message: String) {
        if (debug) println(message)
    }
}

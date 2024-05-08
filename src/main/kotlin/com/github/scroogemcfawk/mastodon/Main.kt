package com.github.scroogemcfawk.mastodon

import com.github.scroogemcfawk.mastodon.api.Mastodon

fun main(args: Array<String>) {
    try {
        val mastodon = Mastodon("techhub.social", true)

        val user = System.getenv("USER_1")!!
        val password = System.getenv("PASSWD")!!

        mastodon.login(user, password)

        println(mastodon.getMe())

        mastodon.postStatus("Test status")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

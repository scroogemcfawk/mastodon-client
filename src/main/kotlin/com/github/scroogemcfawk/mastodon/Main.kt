package com.github.scroogemcfawk.mastodon

import com.github.scroogemcfawk.mastodon.api.Mastodon

fun main(args: Array<String>) {
    try {
        val mastodon = Mastodon("techhub.social", true)
//        mastodon.verifyAppCred()
        // TODO login here
        val timeline = mastodon.getPublicTimeline()
        for (status in timeline.part) {
            println("${status.url} ${status.content}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

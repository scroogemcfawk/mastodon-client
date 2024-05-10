package com.github.scroogemcfawk.mastodon.api

import social.bigbone.api.Pageable
import social.bigbone.api.entity.Account
import social.bigbone.api.entity.Status

interface IMastodon {
    fun getRules(): String
    fun register(username: String, email: String, password: String, agreement: Boolean, locale: String, autologin: Boolean = true, reason: String? = null)
    fun login(username: String, password: String)
    fun logout()
    fun getHomeTimeline(): Pageable<Status>
    fun getPublicTimeline(): Pageable<Status>
    fun searchUser(query: String): List<Account>
    fun getUserByUsername(username: String, hostname: String? = null): Account?
    fun getMe(): Account
    fun postStatus(text: String)

    /**
     * [delayPattern] is a pattern, specified in [ISO-8601 Durations](https://en.wikipedia.org/wiki/ISO_8601#Durations)
     * ```kotlin
     * // this status is delayed by 20 minutes and 49 seconds
     * scheduleStatusAfterDelay("Delayed status", "PT20M49S")
     * ```
     */
    fun scheduleStatusAfterDelay(text: String, delayPattern: String)
}

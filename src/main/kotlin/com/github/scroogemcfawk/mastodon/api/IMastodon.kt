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
    fun getUserProfile(username: String): List<Account>
}

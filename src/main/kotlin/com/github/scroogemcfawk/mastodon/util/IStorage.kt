package com.github.scroogemcfawk.mastodon.util

import social.bigbone.api.entity.Application
import social.bigbone.api.entity.Token

interface IStorage {
    fun getApplication(serverId: String): Application?
    fun saveApplication(serverUrl: String, app: Application)
    fun getRequestToken(clientId: String): Token?
    fun saveRequestToken(clientId: String, token: Token)
    fun getAccessToken(clientId: String, username: String): Token?
    fun saveAccessToken(clientId: String, username: String, token: Token)
}

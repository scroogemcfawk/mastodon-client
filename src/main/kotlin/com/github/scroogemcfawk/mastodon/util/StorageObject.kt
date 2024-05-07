package com.github.scroogemcfawk.mastodon.util

import social.bigbone.api.entity.Application
import social.bigbone.api.entity.Token

typealias ServerUrl = String
typealias ClientId = String
typealias Username = String

data class StorageObject(
    val applications: MutableMap<ServerUrl, Application> = HashMap(),
    val requestTokens: MutableMap<ClientId, Token> = HashMap(),
    val accessTokens: MutableMap<ClientId, MutableMap<Username, Token>> = HashMap()
)

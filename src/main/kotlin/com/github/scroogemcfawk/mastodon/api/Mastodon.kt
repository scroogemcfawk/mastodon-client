package com.github.scroogemcfawk.mastodon.api

import com.github.scroogemcfawk.util.IDebuggable
import com.github.scroogemcfawk.mastodon.util.IStorage
import com.github.scroogemcfawk.mastodon.util.SimpleStorage
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Scope
import social.bigbone.api.entity.Application
import social.bigbone.api.entity.Status
import social.bigbone.api.entity.Token
import social.bigbone.api.exception.BigBoneRequestException
import java.io.File

class Mastodon(
    val hostname: String,
    override val debug: Boolean = false
) : IMastodon, IDebuggable {

    // generic parameters
    companion object {
        private val localRedirectUri = "urn:ietf:wg:oauth:2.0:oob"
        private val fullScope = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL)
        private val clientName = "LUWRAIN Mastodon Client"
    }

    private lateinit var client: MastodonClient
    private lateinit var application: Application
    // if produced by an app -> request token
    // if produced by a user -> access token
    private var requestToken: Token? = null
    private var accessToken: Token? = null

    // current user login or null
    private var login: String? = null
    private var userSeenTheRules: Boolean = false

    private val storage: IStorage = SimpleStorage(File(".mastodon.json"), debug)

    init {
        initClient()
        initApplication()
    }

    private fun initClient() {
        client = MastodonClient.Builder(hostname).build()
        deb("Initialized client: hostname=$hostname")
    }

    private fun authorizeClient(accessToken: Token? = null) {
        val token = accessToken ?: getRequestToken()
        client = MastodonClient.Builder(hostname).accessToken(token.accessToken).build()
        deb("Authorized client: hostname=$hostname, token=$token")
    }

    private fun getRequestToken(): Token {
        requestToken = client.oauth.getAccessTokenWithClientCredentialsGrant(application.clientId!!, application.clientSecret!!, localRedirectUri, fullScope).execute()
        storage.saveRequestToken(application.clientId!!, requestToken!!)
        return requestToken!!
    }


    private fun initApplication() {
        // todo add secret storage
        deb("Initializing application: name=$clientName, redirect=$localRedirectUri, scope=$fullScope")
        try {
            if (tryInitApplicationFromStorage() == null) {
                application = client.apps.createApp(
                    clientName,
                    localRedirectUri,
                    null,
                    fullScope
                ).execute()
                storage.saveApplication(hostname, application)
            }
        } catch (e: BigBoneRequestException) {
            System.err.println("Application initialization failed. Status code: ${e.httpStatusCode}")
        }
        deb("Initialized application: id=${application.clientId}, secret=${application.clientSecret}")
    }

    private fun tryInitApplicationFromStorage(): Application? {
        storage.getApplication(hostname)?.let {
            application = it
            deb("Initialized application from storage.")
            return it
        }
        return null
    }

    private fun tryInitClientFromStorage(clientId: String, username: String): Token? {
        storage.getAccessToken(clientId, username)?.let {
            accessToken = it
            deb("Initialized access token from storage.")
            return it
        }
        return null
    }

    override fun getRules(): String {
        userSeenTheRules = true
        return client.instances.getRules().execute().joinToString("\n")
    }

    override fun register(username: String, email: String, password: String, agreement: Boolean, locale: String, autologin: Boolean, reason: String?) {
        deb("Register user: username=$username email=$email password=$password agreement=$agreement")
        if (!userSeenTheRules) {
            throw IllegalStateException("User has not seen the rules yet. Call getRules() and show rules to the user first.")
        }
        if (requestToken == null) {
            authorizeClient()
        }
        try {
            val localAccessToken = client.accounts.registerAccount(
                username,
                email,
                password,
                agreement,
                "en-US",
                null
            ).execute()
            storage.saveAccessToken(application.clientId!!, username, localAccessToken)
            deb("User has been registered.")
            if (autologin) {
                deb("Auto-login user.")
                authorizeClient(localAccessToken)
            }
        } catch (e: BigBoneRequestException) {
            System.err.println("User register failed. Status code: ${e.httpStatusCode}")
        }
    }

    override fun login(username: String, password: String) {
        if (application.clientId == null || application.clientSecret == null) {
            throw IllegalStateException("Application is not initialized.")
        }
        try {
            if (tryInitClientFromStorage(application.clientId!!, username) == null) {
                accessToken = client.oauth.getUserAccessTokenWithPasswordGrant(
                    application.clientId!!,
                    application.clientSecret!!,
                    localRedirectUri,
                    username,
                    password,
                    fullScope
                ).execute()
                storage.saveAccessToken(application.clientId!!, username, accessToken!!)
            }
            login = username
            // reinit client with user's access token
            authorizeClient(accessToken)
        } catch (e: BigBoneRequestException) {
            System.err.println("User login failed. Status code: ${e.httpStatusCode}")
        }
    }

    override fun logout() {
        login = null
        accessToken = null
    }

    override fun getHomeTimeline(): Pageable<Status> {
        if (login == null) {
            throw IllegalStateException("User is not logged in.")
        }
        return client.timelines.getHomeTimeline().execute()
    }

    override fun getPublicTimeline(): Pageable<Status> {
        return client.timelines.getPublicTimeline().execute()
    }

    fun verifyAppCred(): Application {
        if (requestToken == null) {
            throw IllegalStateException("Application is not authorized.")
        }
        return client.apps.verifyCredentials().execute()
    }
}

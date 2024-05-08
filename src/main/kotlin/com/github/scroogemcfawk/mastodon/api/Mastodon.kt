package com.github.scroogemcfawk.mastodon.api

import com.github.scroogemcfawk.util.IDebuggable
import com.github.scroogemcfawk.mastodon.util.IStorage
import com.github.scroogemcfawk.mastodon.util.SimpleStorage
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Scope
import social.bigbone.api.entity.Account
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
        private const val NO_REDIRECT = "urn:ietf:wg:oauth:2.0:oob"
        private val FULL_SCOPE = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL)
        private const val CLIENT_NAME = "LUWRAIN Mastodon Client"
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
        requestToken = client.oauth.getAccessTokenWithClientCredentialsGrant(application.clientId!!, application.clientSecret!!, NO_REDIRECT, FULL_SCOPE).execute()
        storage.saveRequestToken(application.clientId!!, requestToken!!)
        return requestToken!!
    }

    fun verifyAppCred(): Application {
        if (requestToken == null) {
            throw IllegalStateException("Application is not authorized.")
        }
        return client.apps.verifyCredentials().execute()
    }

    private fun initApplication() {
        // todo add secret storage
        deb("Initializing application: name=$CLIENT_NAME, redirect=$NO_REDIRECT, scope=$FULL_SCOPE")
        try {
            if (tryInitApplicationFromStorage() == null) {
                application = client.apps.createApp(
                    CLIENT_NAME,
                    NO_REDIRECT,
                    null,
                    FULL_SCOPE
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
            accessToken = client.accounts.registerAccount(
                username,
                email,
                password,
                agreement,
                "en-US",
                null
            ).execute()
            storage.saveAccessToken(application.clientId!!, username, accessToken!!)
            deb("User has been registered.")
            if (autologin) {
                deb("Auto-login.")
                login(username, password)
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
                    NO_REDIRECT,
                    username,
                    password,
                    FULL_SCOPE
                ).execute()
                storage.saveAccessToken(application.clientId!!, username, accessToken!!)
            }
            login = username
            deb("Logged in as: $login")
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

    override fun searchUser(username: String): List<Account> {
        val users = ArrayList<Account>()
        users.addAll(
            client.accounts.searchAccounts(query = username, limit = 10).execute()
        )
        return users
    }

    override fun getMe(): Account {
        return client.accounts.verifyCredentials().execute()
    }

    override fun postStatus(text: String) {
        client.statuses.postStatus(text).execute()
    }
}

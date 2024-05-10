package com.github.scroogemcfawk.mastodon.util

import com.github.scroogemcfawk.util.IDebuggable
import com.google.gson.Gson
import social.bigbone.api.entity.Application
import social.bigbone.api.entity.Token
import java.io.File

class SimpleStorage(
    val file: File,
    override val debug: Boolean = false,
    override val timer: Boolean = true
) : IStorage, IDebuggable {

    lateinit var storageObject: StorageObject
    init {
        if (file.exists()) {
            tryLoadFromFile()
        } else {
            storageObject = StorageObject()
            saveToFile()
        }
    }

    private fun tryLoadFromFile() {
        val gson = Gson()
        try {
            storageObject = gson.fromJson(file.readText(), StorageObject::class.java)
        } catch (e: Exception) {
            deb("Failed to load storage from file.")
            storageObject = StorageObject()
        }
    }

    private fun saveToFile() {
        val fout = file.outputStream().bufferedWriter()
        fout.write(Gson().toJson(storageObject))
        fout.close()
    }

    override fun getApplication(serverId: String): Application? {
        return storageObject.applications[serverId]
    }

    override fun saveApplication(serverUrl: String, app: Application) {
        storageObject.applications[serverUrl] = app
        saveToFile()
    }

    override fun getRequestToken(clientId: String): Token? {
        return storageObject.requestTokens[clientId]
    }

    override fun saveRequestToken(clientId: String, token: Token) {
        storageObject.requestTokens[clientId] = token
        saveToFile()
    }

    override fun getAccessToken(clientId: String, username: String): Token? {
        return storageObject.accessTokens[clientId]?.get(username)
    }

    override fun saveAccessToken(clientId: String, username: String, token: Token) {
        // HELL YEAH!
        // tbh it just creates a map, if it was not there already and puts token into it
        storageObject.accessTokens.getOrPut(clientId) {
            HashMap()
        }[username] = token
        saveToFile()
    }
}

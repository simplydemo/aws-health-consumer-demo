package io.github.simplydemo.config

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.*

class AppProperties(
    private val properties: MutableMap<String, Any?> = mutableMapOf()
) : MutableMap<String, Any?> by properties {

    fun <T> get(key: String, defaultValue: T): T {
        @Suppress("UNCHECKED_CAST") return properties.getOrDefault(key, defaultValue) as T
    }

    fun getString(key: String): String? {
        return properties[key] as String?
    }

    override fun toString(): String {
        return properties.toString()
    }

}


fun loadProperties(): Either<Throwable, AppProperties> {
    val profile = System.getenv("PROFILE_ACTIVE") ?: System.getProperty("PROFILE_ACTIVE") ?: "dev"
    val gchatWebhookUrl =
        System.getenv("GCHAT_WEBHOOK_URL") ?: System.getProperty("GCHAT_WEBHOOK_URL") ?: "http://no-webhook"

    // println("================ loadConfig ==============   $profile ")
    val configFileName = "config-$profile.properties"
    val properties = Properties().apply {
        val inputStream = object {}.javaClass.getResourceAsStream("/$configFileName")
        if (inputStream != null) {
            load(inputStream)
        } else {
            return IllegalArgumentException("Configuration file $configFileName not found").left()
        }
    }
    val map: MutableMap<String, Any?> = properties.toMutableMap() as MutableMap<String, Any?>
    map["profiles.active"] = profile
    map["gchat.webhook.url"] = gchatWebhookUrl
    return AppProperties(map).right()
}
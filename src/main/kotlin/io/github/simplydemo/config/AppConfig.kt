package io.github.simplydemo.config

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.io.File
import java.util.*

data class AppConfig(
    val topicArn: String,
    val lambdaArn: String,
    val gchatWebhookUrl: String,
    val severityWarning: String? = "",
    val severityHigh: String? = "",
    val severityCritical: String? = "",
    val severitySubjectHigh: String? = "",
    val severitySubjectCritical: String? = "",
)

fun loadConfig(): Either<Throwable, AppConfig> {
    val profile = System.getenv("profiles.active")
        ?: System.getProperty("profiles.active")
        ?: "dev"
    // println("================ loadConfig ==============   $profile ")
    val configFileName = "config-$profile.properties"
    val configFile: File = when {
        File("src/main/resources/$configFileName").exists() -> File("src/main/resources/$configFileName")
        File("src/test/resources/$configFileName").exists() -> File("src/test/resources/$configFileName")
        else -> throw IllegalArgumentException("Configuration file $configFileName not found")
    }

    return if (configFile.exists()) {
        val properties = Properties().apply { load(configFile.inputStream()) }
        AppConfig(
            topicArn = properties.getProperty("aws.sns.topicArn"),
            lambdaArn = properties.getProperty("aws.sqs.lambdaArn"),
            gchatWebhookUrl = properties.getProperty("gchat.webhook.url"),
            severityWarning = properties.getProperty("severity.warning"),
            severityHigh = properties.getProperty("severity.high"),
            severityCritical = properties.getProperty("severity.critical"),
            severitySubjectHigh = properties.getProperty("severity.subject.high"),
            severitySubjectCritical = properties.getProperty("severity.subject.critical"),
        ).right()
    } else {
        IllegalArgumentException("Configuration file $configFileName not found").left()
    }
}

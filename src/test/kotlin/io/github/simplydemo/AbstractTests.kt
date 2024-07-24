package io.github.simplydemo

import arrow.core.Either
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.simplydemo.config.AppProperties
import io.github.simplydemo.config.loadProperties
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import java.util.*

open class AbstractTests {

    companion object {

        @JvmStatic
        fun toJsonString(filepath: String): String {
            try {
                return object {}.javaClass.getResourceAsStream(filepath)?.bufferedReader().use { it?.readText() ?: "" }
            } catch (e: Exception) {
                println("Error reading file: ${e.message}")
                return ""
            }
        }

        @JvmStatic
        fun sns(subject: String?, message: String): SNSEvent.SNS {
            return SNSEvent.SNS().apply {
                this.message = message
                this.subject = subject
                messageId = UUID.randomUUID().toString().lowercase() // "0469efdc-45be-4026-bbd5-8ec1f0ffbb71"
                timestamp = DateTime.now() // LocalDateTime.now() // "2023-07-11T00:00:00.000Z"
                signature = "EXAMPLE_SIGNATURE"
                topicArn = "arn:aws:sns:REGION:ACCOUNT_ID:TOPIC_NAME"
                signatureVersion = "1"
                signingCertUrl = "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-EXAMPLE.pem"
                unsubscribeUrl =
                    "https://sns.us-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:REGION:ACCOUNT_ID:TOPIC_NAME:SUBSCRIPTION_ID"
                type = "Notification"
            }
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll(): Unit {
            System.setProperty("PROFILE_ACTIVE", "test")
            println("============ beforeAll ========  ")
        }
    }

    val props = loadProperties().fold({ AppProperties() }, { it })

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun toJson(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }


}
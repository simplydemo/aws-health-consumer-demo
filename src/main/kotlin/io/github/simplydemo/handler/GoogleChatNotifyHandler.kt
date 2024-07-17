package io.github.simplydemo.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.simplydemo.gchat.message.CardV2Message
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class GoogleChatNotifyHandler(
    private val webhookUrl: String,
    private val client: OkHttpClient = OkHttpClient(),
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {

    private fun sendMessage(payload: String) {
        val requestBody = payload.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(webhookUrl)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            println("response \n $response")
            println("response.isSuccessful \n ${response.isSuccessful}")
            println("response.isRedirect \n ${response.isRedirect}")
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            println("Message sent successfully")
        }
    }

    fun notifyCard(cardV2: CardV2Message) {
        val payload = objectMapper.writeValueAsString(cardV2)
        println("cardV2: $payload")
        // sendMessage(payload)
    }


    fun notifySimple(message: String) {
        val payload = objectMapper.writeValueAsString(mapOf("text" to message))
        sendMessage(payload)
    }


}
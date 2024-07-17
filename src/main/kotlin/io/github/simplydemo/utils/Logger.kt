package io.github.simplydemo.utils

import arrow.core.Option
import arrow.core.none
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object Logger {
    private val objectMapper = jacksonObjectMapper()

    private lateinit var logger: LambdaLogger

    fun init(logger: LambdaLogger) {
        this.logger = logger
    }

    fun log(message: String, throwable: Option<Throwable> = none()) {
        val logMsg: Map<String, Any> = mapOf(
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        ) + throwable.fold(
            { emptyMap<String, Any>() },
            { mapOf("error" to (it.message ?: "Unknown error")) }
        )
        logger.log(objectMapper.writeValueAsString(logMsg) + "\n")
    }

}
package io.github.simplydemo

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import io.github.simplydemo.config.AppProperties
import io.github.simplydemo.config.loadProperties
import io.github.simplydemo.handler.GoogleChatNotifyHandler
import io.github.simplydemo.payload.CardV2PayloadBuilder
import io.github.simplydemo.utils.Logger

class LambdaHandler : RequestHandler<SNSEvent, Void> {

    private val props = loadProperties().fold({ AppProperties() }, { it })
    private val handler = GoogleChatNotifyHandler(props.getString("gchat.webhook.url") ?: "https://no.webhookurl")
    private fun processSnsEvent(event: SNSEvent) {
        event.records.forEach { record ->
            Logger.log("Received SNS message")
            val card = CardV2PayloadBuilder(props).buildMessage(record.sns)
            handler.notifyCard(card)
        }
    }

    override fun handleRequest(event: SNSEvent, context: Context): Void? {
        Logger.init(context.logger)
        Logger.log("request-id: ${context.awsRequestId}")
        Logger.log("props.toString(): ${props.toString()}")
        processSnsEvent(event)
        return null
    }

}

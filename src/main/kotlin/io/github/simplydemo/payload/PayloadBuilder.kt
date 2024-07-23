package io.github.simplydemo.payload

import com.amazonaws.services.lambda.runtime.events.SNSEvent
import io.github.simplydemo.gchat.message.CardV2Message

interface PayloadBuilder {
    fun buildMessage(sns: SNSEvent.SNS): CardV2Message
}
package io.github.simplydemo

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LambdaHandlerTest : AbstractTests() {

    @Test
    fun testHandleRequest() {
        val snsEvent = mockk<SNSEvent>()
        val context = mockk<Context>()
        val subject =
            "[Action Required] Upcoming changes to recursive loop detection feature in AWS Lambda [AWS Account: 111122223333]"
        val payload = AbstractTests.toJsonString("/health108.json")
        val snsRecord = SNSRecord().withSns(sns(subject, payload)).apply {
                eventSource = "aws:sns"
                eventVersion = "1.0"
                eventSubscriptionArn = "arn:aws:sns:REGION:ACCOUNT_ID:TOPIC_NAME:SUBSCRIPTION_ID"
            }

        // Mocking the records in SNSEvent
        every { context.awsRequestId } returns "894c62ec-e521-4ca9-91f7-a7f8dff17bd1"
        every { context.logStreamName } returns "aws-health-consumer-log"
        every { snsEvent.records } returns listOf(snsRecord)
        every { context.logger } returns object : LambdaLogger {
            override fun log(message: String) {
                print(message)
            }

            override fun log(message: ByteArray) {
                try {
                    System.out.write(message)
                } catch (var3: IOException) {
                    var3.printStackTrace()
                }
            }
        };

        val handler = LambdaHandler()
        val result = handler.handleRequest(snsEvent, context)
        assertTrue { result }
        assertEquals(true, result)
    }
}
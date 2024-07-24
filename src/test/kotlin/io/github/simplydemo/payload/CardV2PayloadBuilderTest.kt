package io.github.simplydemo.payload

import com.amazonaws.services.lambda.runtime.LambdaLogger
import io.github.simplydemo.AbstractTests
import io.github.simplydemo.utils.Logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertEquals

class CardV2PayloadBuilderTest : AbstractTests() {

    @BeforeEach
    fun beforeEach() {
        Logger.init(object : LambdaLogger {
            override fun log(message: String) {
                print(message)
            }

            override fun log(message: ByteArray) {
                try {
                    System.out.write(message)
                } catch (err: IOException) {
                    err.printStackTrace()
                }
            }
        })
    }

    @Test
    fun `test-payload-tc101`() {
        val health108 = AbstractTests.toJsonString("/health108.json")
        val cardV2Message = CardV2PayloadBuilder(props).buildMessage(sns(null, health108))
        val subject = cardV2Message.cardsV2.firstOrNull()?.card?.header?.subtitle
        assertEquals("CUSTOM-EVENT-NOTIFICATION", subject)
    }
}
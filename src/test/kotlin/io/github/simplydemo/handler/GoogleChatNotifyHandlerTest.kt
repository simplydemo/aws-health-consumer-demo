package io.github.simplydemo.handler

import com.amazonaws.services.lambda.runtime.LambdaLogger
import io.github.simplydemo.AbstractTests
import io.github.simplydemo.gchat.message.CardsV2Builder
import io.github.simplydemo.gchat.message.WidgetsBuilder
import io.github.simplydemo.utils.Logger
import io.github.thenovaworks.json.query.JsonQueryHandler
import io.github.thenovaworks.json.query.SqlSession
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.IOException
import kotlin.test.assertTrue

class GoogleChatNotifyHandlerTest {

    /**
     * Edit Configuration
     * select GoogleChatNotifyHandlerTest
     * GCHAT_WEBHOOK_URL : <your-google-hangout-webhook-url>
     */
    private val webHookUrl = System.getenv("GCHAT_WEBHOOK_URL")

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
    fun `testNotify-TC1001`() {
        assertTrue { true }
    }

    @Test
    fun `testNotify-TC1002`() {
        val handler = GoogleChatNotifyHandler(webHookUrl)
        assertDoesNotThrow {
            handler.notifySimple(("This is a Test"))
        }
    }

    @Test
    fun `test-notify-card`() {
        val card = CardsV2Builder()
            .cardId("d553e1a6-a287-4279-9bd7-1a62b47ec24c")
            .header(
                "AWS Health [US-EAST-1]",
                "[REMINDER] Amazon Aurora MySQL 2 (with MySQL 5.7 compatibility) is reaching end of Standard Support [AWS Account: 111122223333] [US-EAST-1]",
                "https://www.iconsdb.com/icons/preview/red/alarm-clock-xxl.png"
            )
            .addSection(
                header = "<font color=\"#ff0000\"><strong>INFO</strong></font>",
                widgets = WidgetsBuilder().addTextParagraph(
                    "<font color=\"#323232\">안녕하세요,\n\n[리소스가 해결되지 않은 경우 AWS Health는 정기적으로 이 커뮤니케이션에 대한 알림을 트리거할 수 있습니다.]\n\n고객님께 이 메시지가 표시되는 이유는 ...</font>"
                ).build()
            )
            .addSection(
                header = "<font color=\"#0a122e\"><strong>영향 받는 리소스</strong></font>",
                collapsible = true,
                widgets = WidgetsBuilder().addTextParagraph("<font color=\"#800000\">aaa \n bbb\nccc</a>").build()
            )
            .addSection(
                header = "",
                widgets = WidgetsBuilder().addButton("AWS Console", "https://example.com").build()
            ).build()
        val handler = GoogleChatNotifyHandler(webHookUrl)
        assertDoesNotThrow {
            handler.notifyCard(card)
        }
    }

    @Test
    fun `testNotify-TC1004-health`() {

        val jsonText = AbstractTests.toJsonString("/health104.json")
        val session = SqlSession(JsonQueryHandler("health", jsonText))
        val sql = """
select  version,
        id,
        detail-type,
        source,
        account,
        time,
        region,
        resources,
        detail.eventArn,
        detail.service,
        detail.eventTypeCode,
        detail.eventTypeCategory,
        detail.eventScopeCode,
        detail.communicationId,
        detail.startTime,
        detail.lastUpdatedTime,
        detail.statusCode,
        detail.eventRegion,
        detail.eventDescription,
        detail.affectedEntities,
        detail.affectedAccount
from    health            
        """
        // println(session.getKeys(2))
        val result = session.queryForObject(sql)
        val time = result.getDateTime("time")
        val service = result.getString("detail.service")
        val startTime = result.getString("detail.startTime")

        println(
            """
service:            $service            
time   :            $time            
detail.startTime:   $startTime            
        """.trimIndent()
        )

        assertTrue { true }
    }
}
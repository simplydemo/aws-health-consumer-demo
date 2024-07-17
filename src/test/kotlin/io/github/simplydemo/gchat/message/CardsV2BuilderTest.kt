package io.github.simplydemo.gchat.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CardsV2BuilderTest {

    private val mapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `test-tc101`() {

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

        println(card)


        assertEquals(1, card.cardsV2.size)
        assertEquals("d553e1a6-a287-4279-9bd7-1a62b47ec24c", card.cardsV2.firstOrNull()?.cardId)
        assertEquals("AWS Health [US-EAST-1]", card.cardsV2.firstOrNull()?.card?.header?.title)

        val json = mapper.writeValueAsString(card)
        println(json)
    }

}
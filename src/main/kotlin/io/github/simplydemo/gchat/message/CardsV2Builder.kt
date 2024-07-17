package io.github.simplydemo.gchat.message

data class Header(
    val title: String,
    val subtitle: String,
    val imageUrl: String? = null,
    val imageType: String? = "CIRCLE"
)

data class TextParagraph(val text: String)

data class OnClick(val openLink: OpenLink)

data class OpenLink(val url: String)

data class Button(val text: String, val onClick: OnClick)

data class ButtonList(val buttons: List<Button>)

data class Widget(
    val textParagraph: TextParagraph? = null,
    val buttonList: ButtonList? = null
)

data class Section(
    val header: String,
    val widgets: List<Widget>,
    val collapsible: Boolean? = false,
    val uncollapsibleWidgetsCount: Int? = 1
)

data class Card(
    val header: Header,
    val sections: List<Section>
)

data class CardV2(
    val cardId: String,
    val card: Card
)

data class CardV2Message(val cardsV2: List<CardV2>)

class WidgetsBuilder {
    private val widgets: MutableList<Widget> = mutableListOf()

    fun addTextParagraph(text: String): WidgetsBuilder {
        val widget = Widget(textParagraph = TextParagraph(text))
        widgets.add(widget)
        return this
    }

    fun addButton(text: String, url: String): WidgetsBuilder {
        val button = Button(text, OnClick(OpenLink(url)))
        val widget = Widget(buttonList = ButtonList(listOf(button)))
        widgets.add(widget)
        return this
    }

    fun build(): List<Widget> {
        return widgets
    }
}

class CardsV2Builder {
    private var cardId: String = ""
    private lateinit var header: Header
    private val sections: MutableList<Section> = mutableListOf()

    fun cardId(id: String): CardsV2Builder {
        this.cardId = id
        return this
    }

    fun header(title: String, subtitle: String, imageUrl: String): CardsV2Builder {
        this.header = Header(title, subtitle, imageUrl)
        return this
    }

    fun addSection(
        header: String,
        widgets: List<Widget>,
        collapsible: Boolean? = false,
        uncollapsibleWidgetsCount: Int? = 1
    ): CardsV2Builder {
        this.sections.add(Section(header, widgets, collapsible, uncollapsibleWidgetsCount))
        return this
    }

    fun build(): CardV2Message {
        val card = Card(header, sections)
        val cardV2 = CardV2(cardId, card)
        return CardV2Message(listOf(cardV2))
    }
}

/*
fun main() {
    val section1Widgets = WidgetsBuilder()
        .addTextParagraph("Section 1 Text")
        .build()

    val section2Widgets = WidgetsBuilder()
        .addTextParagraph("Section 2 Text")
        .build()

    val section3Widgets = WidgetsBuilder()
        .addButton("Button Text", "https://example.com")
        .build()

    val builder = CardV2MessageBuilder()

    val message = builder
        .cardId("card-123")
        .header("Card Title", "Card Subtitle", "https://example.com/image.png")
        .addSection(
            header = "Section 1 Header",
            collapsible = false,
            uncollapsibleWidgetsCount = 1,
            widgets = section1Widgets
        )
        .addSection(
            header = "Section 2 Header",
            collapsible = true,
            uncollapsibleWidgetsCount = 0,
            widgets = section2Widgets
        )
        .addSection(
            header = "",
            collapsible = false,
            uncollapsibleWidgetsCount = 0,
            widgets = section3Widgets
        )
        .build()

    println(message)
}
*/
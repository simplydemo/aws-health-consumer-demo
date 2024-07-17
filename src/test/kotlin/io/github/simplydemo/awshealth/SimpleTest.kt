package io.github.simplydemo.awshealth

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Simple {

    fun hello(name: Option<String> = None): String =
        name.map { "hello, $it" }.getOrElse { "hello" }

    fun helloMap(name: String, param: Map<String, Any> = emptyMap()): String {
        return if (param.isNotEmpty()) {
            "hello, $name, parameters is ${param.keys}"
        } else {
            "hello, $name"
        }
    }
}

class SimpleTest {
    private val simple = Simple()

    @Test
    fun `test-hello`() {
        assertEquals("hello", simple.hello())
        assertEquals("hello, Simple", simple.hello(Some("Simple")))
    }

    @Test
    fun `test-map-hello`() {
        // println(simple.helloMap("Bob", mapOf("age" to 30, "city" to "Seoul")))
        assertEquals("hello, Alice", simple.helloMap("Alice"))
        assertEquals(
            "hello, Bob, parameters is [age, city]",
            simple.helloMap("Bob", mapOf("age" to 30, "city" to "Seoul"))
        )
    }

    @Test
    fun `test-regex-current-severity-level-tc101`() {
        val text = """
        Current severity level: Informational

        Elevated delays in newly applied tags

        [08:09 AM PDT] We have resolved the cause of the delays in newly applied resource tags becoming visible in Describe API calls in the US-EAST-1 Region.
    """.trimIndent()

        // 처음 두 줄만 추출
        val firstTwoLines = text.lines().take(2).joinToString("\n")

        val pattern = Regex("""Current severity level:\s+(.+)""")
        val match = pattern.find(firstTwoLines)
        val severity = match?.groupValues?.get(1) ?: "NA"
        assertEquals("Informational", severity)
    }

    @Test
    fun `test-regex-current-severity-level-tc102`() {
        val text = """
 Current severity level: Operating normally

[RESOLVED] Elevated delays in newly applied tags

[08:17 AM PDT] Between 6:07 AM and 7:45 AM PDT,
    """

        // 처음 두 줄만 추출
        val firstTwoLines = text.lines().take(2).joinToString("\n")

        val pattern = Regex("""Current severity level:\s+(.+)""")
        val match = pattern.find(firstTwoLines)
        val severity = match?.groupValues?.get(1) ?: "NA"
        assertEquals("Operating normally", severity)
    }
}
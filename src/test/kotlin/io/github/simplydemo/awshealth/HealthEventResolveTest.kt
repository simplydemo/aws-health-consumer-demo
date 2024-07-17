package io.github.simplydemo.awshealth

import io.github.simplydemo.AbstractTests
import io.github.simplydemo.gchat.message.CardsV2Builder
import io.github.simplydemo.gchat.message.WidgetsBuilder
import io.github.simplydemo.utils.MessageUtils.Companion.isPatternMatched
import io.github.simplydemo.utils.MessageUtils.Companion.severityColor
import io.github.thenovaworks.json.query.JsonQueryHandler
import io.github.thenovaworks.json.query.SqlSession
import org.junit.jupiter.api.Test


class HealthEventResolveTest : AbstractTests() {

    private val sql = """
select  id,
        account,
        detail-type,
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
        detail.affectedAccount,
        detail.affectedEntities
from    health        
    """


    @Test
    fun `test-health-event-106`() {
        val subject =
            "[Action Required] Upcoming changes to recursive loop detection feature in AWS Lambda [AWS Account: 129335065553]"
        val healthMsg = AbstractTests.toJsonString("/health106.json")
        val sns = sns(subject, healthMsg)
        val sqlSession = SqlSession(JsonQueryHandler("health", sns.message))
        val rs = sqlSession.queryForObject(this.sql)
        // println("=====================================")
        // println(rs)
        // println("=====================================")
        // println("keys: ${sqlSession.getKeys(2)}")

        val region = rs.getString("detail.eventRegion").takeIf { !it.isNullOrBlank() } ?: rs.getString("region")
        val title = "${rs.getString("detail-type")} [${region.uppercase()}]"
        val affectedAccount =
            rs.getString("detail.affectedAccount").takeIf { !it.isNullOrBlank() } ?: rs.getString("account")
        val category = rs.getString("detail.eventTypeCategory")
        val service = rs.getString("detail.service")
        val description = rs.getList("detail.eventDescription")?.firstOrNull()?.get("latestDescription") as String ?: ""
        val affectedEntities = rs.getList("detail.affectedEntities")

        val severityCriticalSubject: List<String>? = config.severitySubjectCritical?.split(",")?.map { it.trim() }
        val severityHighSubject: List<String>? = config.severitySubjectHigh?.split(",")?.map { it.trim() }
        val severityCriticalDesc: List<String>? = config.severityCritical?.split(",")?.map { it.trim() }
        val severityHighDesc: List<String>? = config.severityHigh?.split(",")?.map { it.trim() }
        val severityWarningDesc: List<String>? = config.severityWarning?.split(",")?.map { it.trim() }

        var severity = when {
            isPatternMatched(subject, severityCriticalSubject) -> "CRITICAL"
            isPatternMatched(subject, severityHighSubject) -> "HIGH"
            isPatternMatched(description, severityCriticalDesc) -> "CRITICAL"
            isPatternMatched(description, severityHighDesc) -> "HIGH"
            isPatternMatched(description, severityWarningDesc) -> "WARNING"
            else -> "INFO"
        }
        val severityColor = severityColor(severity)

        val header =
            "<font color=\"${severityColor}\"><strong>${severity}</strong></font> (service: ${service}, category: ${category})\nAccount: $affectedAccount"

        val builder = CardsV2Builder()
            .cardId(rs.getString("id"))
            .header(
                title, subject, "https://www.iconsdb.com/icons/preview/red/alarm-clock-xxl.png"
            )
            .addSection(
                header = header,
                widgets = WidgetsBuilder().addTextParagraph("<font color=\"#323232\">${description}</font>").build()
            )

        if (affectedEntities?.isNotEmpty() == true) {
            val affectedRcs = StringBuilder()
            affectedEntities.forEach() { v ->
                val entityValue = v["entityValue"] as String
                val status = v["status"] as String
                val lastUpdatedTime = v["lastUpdatedTime"] as String
                affectedRcs.append("\n")
                affectedRcs.append(entityValue)
                if (status != null) {
                    affectedRcs.append(" - ")
                    affectedRcs.append(status)
                }
                if (lastUpdatedTime != null) {
                    affectedRcs.append(" - ")
                    affectedRcs.append(lastUpdatedTime)
                }
            }
            builder.addSection(
                header = "<font color=\"#0a122e\"><strong>Affected Resources</strong></font>",
                collapsible = true,
                widgets = WidgetsBuilder().addTextParagraph("<font color=\"#800000\">$affectedRcs</a>").build()
            )
        }

        val card = builder.build()
        val payload = toJson(card)
        println(payload)
    }

    @Test
    fun `test-health-event-107`() {
        val subject =
            "[Immediate Action] Upcoming changes to recursive loop detection feature in AWS Lambda [AWS Account: 129335065553]"
        val healthMsg = AbstractTests.toJsonString("/health107.json")
        val sns = sns("", healthMsg)
        val sqlSession = SqlSession(JsonQueryHandler("health", sns.message))
        val rs = sqlSession.queryForObject(this.sql)

        val region = rs.getString("detail.eventRegion").takeIf { !it.isNullOrBlank() } ?: rs.getString("region")
        val title = "${rs.getString("detail-type")} [${region.uppercase()}]"
        val affectedAccount =
            rs.getString("detail.affectedAccount").takeIf { !it.isNullOrBlank() } ?: rs.getString("account")
        val category = rs.getString("detail.eventTypeCategory")
        val service = rs.getString("detail.service")
        val description = rs.getList("detail.eventDescription")?.firstOrNull()?.get("latestDescription") as String ?: ""
        val affectedEntities = rs.getList("detail.affectedEntities")

        val severityCriticalSubject: List<String>? = config.severitySubjectCritical?.split(",")?.map { it.trim() }
        val severityHighSubject: List<String>? = config.severitySubjectHigh?.split(",")?.map { it.trim() }
        val severityCriticalDesc: List<String>? = config.severityCritical?.split(",")?.map { it.trim() }
        val severityHighDesc: List<String>? = config.severityHigh?.split(",")?.map { it.trim() }
        val severityWarningDesc: List<String>? = config.severityWarning?.split(",")?.map { it.trim() }

        var severity = when {
            isPatternMatched(subject, severityCriticalSubject) -> "CRITICAL"
            isPatternMatched(subject, severityHighSubject) -> "HIGH"
            isPatternMatched(description, severityCriticalDesc) -> "CRITICAL"
            isPatternMatched(description, severityHighDesc) -> "HIGH"
            isPatternMatched(description, severityWarningDesc) -> "WARNING"
            else -> "INFO"
        }
        val severityColor = severityColor(severity)

        val header =
            "<font color=\"${severityColor}\"><strong>${severity}</strong></font> (service: ${service}, category: ${category})\nAccount: $affectedAccount"

        val builder = CardsV2Builder()
            .cardId(rs.getString("id"))
            .header(
                title, subject, "https://www.iconsdb.com/icons/preview/red/alarm-clock-xxl.png"
            )
            .addSection(
                header = header,
                widgets = WidgetsBuilder().addTextParagraph("<font color=\"#323232\">${description}</font>").build()
            )

        if (affectedEntities?.isNotEmpty() == true) {
            val affectedRcs = StringBuilder()
            affectedEntities.forEach() { v ->
                val entityValue = v["entityValue"] as String
                val status = v["status"] as String
                val lastUpdatedTime = v["lastUpdatedTime"] as String
                affectedRcs.append("\n")
                affectedRcs.append(entityValue)
                if (status != null) {
                    affectedRcs.append(" - ")
                    affectedRcs.append(status)
                }
                if (lastUpdatedTime != null) {
                    affectedRcs.append(" - ")
                    affectedRcs.append(lastUpdatedTime)
                }
            }
            builder.addSection(
                header = "<font color=\"#0a122e\"><strong>Affected Resources</strong></font>",
                collapsible = true,
                widgets = WidgetsBuilder().addTextParagraph("<font color=\"#800000\">$affectedRcs</a>").build()
            )
        }
        val card = builder.build()
        val payload = toJson(card)
        println(payload)
    }

}


/*
val card = CardsV2Builder()


            .addSection(
                header = "",
                widgets = WidgetsBuilder().addButton("AWS Console", "https://example.com").build()
            ).build()
 */

package io.github.simplydemo.payload

import com.amazonaws.services.lambda.runtime.events.SNSEvent
import io.github.simplydemo.config.AppProperties
import io.github.simplydemo.gchat.message.CardV2Message
import io.github.simplydemo.gchat.message.CardsV2Builder
import io.github.simplydemo.gchat.message.WidgetsBuilder
import io.github.simplydemo.utils.Logger
import io.github.simplydemo.utils.MessageUtils
import io.github.thenovaworks.json.query.JsonQueryHandler
import io.github.thenovaworks.json.query.SqlSession

class CardV2PayloadBuilder(private val props: AppProperties) : PayloadBuilder {

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
from    health """

    override fun buildMessage(sns: SNSEvent.SNS): CardV2Message {
        val subject = sns.subject ?: "CUSTOM-EVENT-NOTIFICATION"
        val sqlSession = SqlSession(JsonQueryHandler("health", sns.message))
        val rs = sqlSession.queryForObject(this.sql)

        val region = rs.getString("detail.eventRegion").takeIf { it.isNotBlank() } ?: rs.getString("region")
        val title = "${rs.getString("detail-type")} [${region.uppercase()}]"
        val affectedAccount =
            rs.getString("detail.affectedAccount").takeIf { it.isNotBlank() } ?: rs.getString("account")
        val category = rs.getString("detail.eventTypeCategory")
        val service = rs.getString("detail.service")
        val description = rs.getList("detail.eventDescription")?.firstOrNull()?.get("latestDescription") as String
        val affectedEntities = rs.getList("detail.affectedEntities")

        val severityCriticalSubject: List<String>? =
            props.getString("severity.subject.critical")?.split(",")?.map { it.trim() }
        val severityHighSubject: List<String>? = props.getString("severity.subject.high")?.split(",")?.map { it.trim() }
        val severityCriticalDesc: List<String>? = props.getString("severity.critical")?.split(",")?.map { it.trim() }
        val severityHighDesc: List<String>? = props.getString("severity.high")?.split(",")?.map { it.trim() }
        val severityWarningDesc: List<String>? = props.getString("severity.warning")?.split(",")?.map { it.trim() }

        val severity = when {
            MessageUtils.isPatternMatched(subject, severityCriticalSubject) -> "CRITICAL"
            MessageUtils.isPatternMatched(subject, severityHighSubject) -> "HIGH"
            MessageUtils.isPatternMatched(description, severityCriticalDesc) -> "CRITICAL"
            MessageUtils.isPatternMatched(description, severityHighDesc) -> "HIGH"
            MessageUtils.isPatternMatched(description, severityWarningDesc) -> "WARNING"
            else -> "INFO"
        }
        val severityColor = MessageUtils.severityColor(severity)

        val header =
            "<font color=\"${severityColor}\"><strong>${severity}</strong></font> (service: ${service}, category: ${category})\nAccount: $affectedAccount"

        val builder = CardsV2Builder()
            .cardId(rs.getString("id"))
            .header(
                title, subject, "https://www.iconsdb.com/icons/preview/red/alarm-clock-xxl.png"
            ).addSection(
                header = header,
                widgets = WidgetsBuilder().addTextParagraph("<font color=\"#323232\">${description}</font>").build()
            )

        Logger.log("affectedEntities: $affectedEntities")
        Logger.log("affectedEntities.size: ${affectedEntities?.size}")

        if (affectedEntities?.isNotEmpty() == true) {
            val affectedRcs = StringBuilder()
            affectedEntities.forEach() { row ->
                val entityValue = row["entityValue"] as String?
                val status = row["status"] as String?
                val lastUpdatedTime = row["lastUpdatedTime"] as String?
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
        return card
    }
}
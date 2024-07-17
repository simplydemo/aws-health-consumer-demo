package io.github.simplydemo

import com.amazonaws.Request
import com.amazonaws.Response
import com.amazonaws.handlers.RequestHandler2
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS
import io.github.simplydemo.config.AppConfig
import io.github.simplydemo.config.loadConfig
import io.github.simplydemo.gchat.message.CardV2Message
import io.github.simplydemo.gchat.message.CardsV2Builder
import io.github.simplydemo.gchat.message.WidgetsBuilder
import io.github.simplydemo.handler.GoogleChatNotifyHandler
import io.github.simplydemo.utils.Logger
import io.github.simplydemo.utils.MessageUtils
import io.github.thenovaworks.json.query.JsonQueryHandler
import io.github.thenovaworks.json.query.SqlSession
import software.amazon.awssdk.services.sns.SnsClient

class LambdaHandler : RequestHandler2() {

    // private val objectMapper = jacksonObjectMapper()

    private val snsClient: SnsClient = SnsClient.builder()
        // .region(Region.of("region"))
        .build()

    private val config = loadConfig().fold({ AppConfig("", "", "") }, { config -> config })

    override fun beforeRequest(request: Request<*>) {
        // 여기에 요청 전 처리 로직을 추가할 수 있습니다
    }

    override fun afterResponse(request: Request<*>, response: Response<*>) {
        // 여기에 응답 후 처리 로직을 추가할 수 있습니다
    }

    override fun afterError(request: Request<*>, response: Response<*>, e: Exception) {
        // 여기에 오류 처리 로직을 추가할 수 있습니다
    }

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


    private fun buildMessage(sns: SNS): CardV2Message {
        val subject = sns.subject
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

        val severityCriticalSubject: List<String>? = config.severitySubjectCritical?.split(",")?.map { it.trim() }
        val severityHighSubject: List<String>? = config.severitySubjectHigh?.split(",")?.map { it.trim() }
        val severityCriticalDesc: List<String>? = config.severityCritical?.split(",")?.map { it.trim() }
        val severityHighDesc: List<String>? = config.severityHigh?.split(",")?.map { it.trim() }
        val severityWarningDesc: List<String>? = config.severityWarning?.split(",")?.map { it.trim() }

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

        println("affectedEntities: $affectedEntities")
        println("affectedEntities.size: ${affectedEntities?.size}")

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

    private fun processSnsEvent(event: SNSEvent) {
        event.records.forEach { record ->
            Logger.log("Received SNS message")
            val webHookUrl = config.gchatWebhookUrl
            val card = buildMessage(record.sns)
            val handler = GoogleChatNotifyHandler(webHookUrl)
            handler.notifyCard(card)
        }
    }

    fun handleRequest(event: SNSEvent, context: Context): Boolean {
        Logger.init(context.logger)
        Logger.log("request-id: ${context.awsRequestId}")
        Logger.log("config.topicArn: ${config.topicArn}")
        Logger.log("config.lambdaArn: ${config.lambdaArn}")
        Logger.log("config.gchatWebhookUrl: ${config.gchatWebhookUrl}")
        processSnsEvent(event)
        return true
    }

}

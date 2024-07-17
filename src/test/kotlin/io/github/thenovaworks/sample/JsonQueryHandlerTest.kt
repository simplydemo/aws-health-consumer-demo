package io.github.thenovaworks.sample

import io.github.simplydemo.AbstractTests
import io.github.thenovaworks.json.query.JsonQueryHandler
import io.github.thenovaworks.json.query.SqlSession
import org.junit.jupiter.api.Test

class JsonQueryHandlerTest {

    @Test
    fun `test-query`(): Unit {
        val json = AbstractTests.toJsonString("/health101.json")
        val sqlSession = SqlSession(JsonQueryHandler("HEALTH", json))
        val record = sqlSession.queryForObject("select id, detail.service, detail.statusCode, region from HEALTH")
        println("record: $record")
    }

    @Test
    fun `test-query-with-parameter`(): Unit {
        val json = AbstractTests.toJsonString("/health101.json")
        val sqlSession = SqlSession(JsonQueryHandler("HEALTH", json))
        val sql = "select id, detail.service, detail.statusCode, region from HEALTH where id = :id"
        val record = sqlSession.queryForObject(sql, mapOf("id" to "7bf73129-1428-4cd3-a780-95db273d1602"))
        println("record: $record")
    }

    @Test
    fun `test-query-with-conditions-and`(): Unit {
        val json = AbstractTests.toJsonString("/health101.json")
        val sqlSession = SqlSession(JsonQueryHandler("HEALTH", json))
        val params = mapOf(
            "id" to "7bf73129-1428-4cd3-a780-95db273d1602",
            // "statusCode" to "open"
            "statusCode" to "closed"
        )
        val sql =
            "select id, detail.service, detail.statusCode, region from HEALTH where id = :id and detail.statusCode = :statusCode"
        val record = sqlSession.queryForObject(sql, params)
        println("record: $record")
    }

    @Test
    fun `test-query-with-conditions-or`(): Unit {
        val json = AbstractTests.toJsonString("/health101.json")
        val sqlSession = SqlSession(JsonQueryHandler("HEALTH", json))
        val params = mapOf(
            "id" to "7bf73129-1428-4cd3-a780-95db273d1602",
            "source" to "aws.health",
            "statusCode" to "closed"
        )
        val sql =
            "select id, detail.service, detail.statusCode, region from HEALTH where id = :id or detail.statusCode = :statusCode"
        val record = sqlSession.queryForObject(sql, params)
        println("record: $record")
    }

    @Test
    fun `test-query-with-conditions-multi`(): Unit {
        val json = AbstractTests.toJsonString("/health101.json")
        val sqlSession = SqlSession(JsonQueryHandler("HEALTH", json))
        val params = mapOf(
            "id" to "7bf73129-1428-4cd3-a780-95db273d1602",
            "source" to "aws.health",
            "statusCode" to "closed"
        )
        val sql =
            "select id, source, detail.service, detail.statusCode, region from HEALTH where id = :id and source = :source or detail.statusCode = :statusCode"
        val record = sqlSession.queryForObject(sql, params)
        println("record: $record")
    }

    @Test
    fun `test-query-102-with-conditions`(): Unit {
        val json = AbstractTests.toJsonString("/health102.json")
        val sqlSession = SqlSession(JsonQueryHandler("HEALTH", json))
        val sql = """
select  time, 
        resources, 
        id, 
        source,
        detail.service,
        detail.statusCode,
        detail.affectedEntities, 
        region 
from    HEALTH     
where   id = '26005bdb-b6eb-466d-920c-3ab19b1d7ea2' 
and     source = 'aws.health'
        """
        val record = sqlSession.queryForObject(sql)
        println("record: $record")
    }

    @Test
    fun `test-query-102-with-condition-param`(): Unit {
        val json = AbstractTests.toJsonString("/health102.json")
        val sqlSession = SqlSession(JsonQueryHandler("HEALTH", json))
        val params = mapOf(
            "id" to "26005bdb-b6eb-466d-920c-3ab19b1d7ea2",
            "source" to "aws.health",
            "statusCode" to "open"
        )
        val sql =
            "select time, time_01, resources, detail.affectedEntities, id, source, detail.service, detail.statusCode, region from HEALTH where id = :id and source = :source and detail.statusCode = :statusCode"
        val record = sqlSession.queryForObject(sql, params)
        println("record: $record")
    }
}

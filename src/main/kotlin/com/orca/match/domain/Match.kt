package com.orca.match.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "matches")
data class Match(
    @Id
    val id: String? = null,
    val scheduledAt: Instant,
    val venue: String,
    val cost: Int,
    val content: String,
    val status: MatchStatus = MatchStatus.PENDING,
    val home: MatchResult,
    val away: MatchResult? = null,
    val createdAt: Instant = Instant.now()
)

enum class MatchStatus(val value: String) {
    PENDING("매칭 대기"),
    MATCHED("매칭 확정"),
    COMPLETED("경기 종료")
}

data class MatchResult(
    val clubId: String,
    val records: MutableList<PlayerRecord> = mutableListOf(),
    val resultType: ResultType? = null,
    val mannerPoint: Double = 0.0
)

data class PlayerRecord(
    val id: String,
    val name: String,
    val goal: Int = 0,
    val assist: Int = 0
)

enum class ResultType(val value: String) {
    WIN("승"),
    LOSE("패"),
    DRAW("무승부")
}
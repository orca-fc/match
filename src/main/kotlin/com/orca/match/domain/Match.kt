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
    val records: List<String> = mutableListOf(),
    val createdAt: Instant = Instant.now()
)

enum class MatchStatus(val value: String) {
    PENDING("매칭 대기"),
    MATCHED("매칭 확정"),
    COMPLETED("경기 종료")
}
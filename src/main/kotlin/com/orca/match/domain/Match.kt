package com.orca.match.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "matches")
data class Match(
    @Id
    val id: ObjectId? = null,
    val scheduledAt: Instant,
    val venue: String,
    val cost: Int,
    val content: String,
    val status: MatchStatus = MatchStatus.PENDING,
    val records: List<ObjectId> = mutableListOf(),
): Auditable()

enum class MatchStatus(val value: String) {
    PENDING("매칭 대기"),
    MATCHED("매칭 확정"),
    COMPLETED("경기 종료")
}
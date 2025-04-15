package com.orca.match.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "match_records")
data class MatchRecord(
    @Id
    val id: String? = null,
    val matchId: String,
    val clubId: String,
    val teamType: TeamType = TeamType.HOME,
    val records: MutableList<PlayerRecord> = mutableListOf(),
    val resultType: ResultType? = null,
    val mannerPoint: Double = 0.0
)

enum class TeamType {
    HOME,
    AWAY
}

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
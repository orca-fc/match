package com.orca.match.api

import com.orca.match.service.MatchCreateCommand
import com.orca.match.util.convertMatchDateFormant
import java.time.Instant

data class MatchCreateRequest(
    val clubId: String,
    val date: String,
    val time: String,
    val venue: String,
    val cost: Int,
    val content: String
) {
    fun toCommand(): MatchCreateCommand {
        return MatchCreateCommand(
            clubId = this.clubId,
            scheduledAt = convertMatchDateFormant(date, time),
            venue = this.venue,
            cost = this.cost,
            content = this.content,
        )
    }
}

data class MatchResponse(
    val id: String,
    val scheduledAt: String,
    val venue: String,
    val cost: Int,
    val content: String,
    val status: String,
    val home: MatchResultResponse,
    val away: MatchResultResponse?,
    val createdAt: Instant,
)

data class MatchResultResponse(
    val clubId: String,
    val records: List<PlayerRecordResponse>,
    val resultType: String?,
    val mannerPoint: Double,
)

data class PlayerRecordResponse(
    val id: String,
    val name: String,
    val goal: Int,
    val assist: Int,
)
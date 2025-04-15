package com.orca.match.api

import com.orca.match.domain.MatchStatus
import com.orca.match.service.CreateMatchCommand
import com.orca.match.service.JoinMatchCommand
import com.orca.match.service.MatchQuery
import com.orca.match.util.convertMatchDateFormant
import jakarta.validation.constraints.Pattern
import org.springframework.data.domain.Sort
import java.time.Instant

data class MatchCreateRequest(
    val clubId: String,
    val date: String,
    val time: String,
    val venue: String,
    val cost: Int,
    val content: String
) {
    fun toCommand(): CreateMatchCommand {
        return CreateMatchCommand(
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
    val records: List<String>,
    val createdAt: Instant,
)

data class MatchRecordResponse(
    val id: String,
    val matchId: String,
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

data class MatchCriteria(
    val status: MatchStatus?,
    val sortDirection: List<
            @Pattern(
                regexp = "^(created|scheduledAt) (asc|desc)$",
                message = "정렬 가능한 필드 (created, scheduledAt), 정렬기준 (asc || desc), 공백으로 구분 \n ex) 'created asc'"
            ) String> = listOf()
) {
    fun toQuery(): MatchQuery {
        return MatchQuery(
            status = this.status,
            sortOptions = this.sortDirection.map {
                val dir = it.split(" ")
                dir[0] to if (dir[1].lowercase() == "asc") Sort.Direction.ASC else Sort.Direction.DESC
            }
        )
    }
}

data class JoinMatchRequest(
    val matchId: String,
    val clubId: String,
    val playerId: String,
    val playerName: String
)

fun JoinMatchRequest.toCommand(): JoinMatchCommand {
    return JoinMatchCommand(
        matchId = this.matchId,
        clubId = this.clubId,
        playerId = this.playerId,
        playerName = this.playerName
    )
}
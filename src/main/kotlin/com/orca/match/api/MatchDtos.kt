package com.orca.match.api

import com.orca.match.domain.MatchStatus
import com.orca.match.service.CancelMatchCommand
import com.orca.match.service.CreateMatchCommand
import com.orca.match.service.JoinMatchCommand
import com.orca.match.service.MatchQuery
import com.orca.match.util.convertMatchDateFormant
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import org.springframework.data.domain.Sort
import java.time.Instant

@Schema(description = "매칭 생성 RequestDTO")
data class MatchCreateRequest(
    @field:Schema(description = "Club ID")
    val clubId: String,
    @field:Schema(description = "경기 날짜", example = "yyyy-MM-dd")
    val date: String,
    @field:Schema(description = "경기 시간", example = "HH:mm")
    val time: String,
    @field:Schema(description = "경기 장소")
    val venue: String,
    @field:Schema(description = "구장 대여료")
    val cost: Int,
    @field:Schema(description = "내용")
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

@Schema(description = "Match ResponseDTO")
data class MatchResponse(
    @field:Schema(description = "Match ID")
    val id: String,
    @field:Schema(description = "경기 날짜")
    val scheduledAt: String,
    @field:Schema(description = "경기 장소")
    val venue: String,
    @field:Schema(description = "구장 대여료")
    val cost: Int,
    @field:Schema(description = "내용")
    val content: String,
    @field:Schema(description = "매칭 상태 (PENDING / MATCHED / COMPLETED)")
    val status: String,
    @field:Schema(description = "MatchRecord ID 목록 (HOME / AWAY)")
    val records: List<String>,
    @field:Schema(description = "매칭 생성 날짜")
    val createdAt: Instant,
)

@Schema(description = "MatchRerord Response DTO")
data class MatchRecordResponse(
    @field:Schema(description = "MatchRecord ID")
    val id: String,
    @field:Schema(description = "Match ID")
    val matchId: String,
    @field:Schema(description = "Club ID")
    val clubId: String,
    @field:Schema(description = "Team Type (HOME / AWAY)")
    val teamType: String,
    @field:Schema(description = "선수 기록")
    val records: List<PlayerRecordResponse>,
    @field:Schema(description = "경기 결과")
    val resultType: String?,
    @field:Schema(description = "매너 점수")
    val mannerPoint: Double,
)

@Schema(description = "PlayerRecord ResponseDTO")
data class PlayerRecordResponse(
    @field:Schema(description = "Player ID")
    val id: String,
    @field:Schema(description = "Player 이름")
    val name: String,
    @field:Schema(description = "득점")
    val goal: Int,
    @field:Schema(description = "도움")
    val assist: Int,
)

@Schema(description = "Match 조회 조건")
data class MatchCriteria(
    @field:Schema(description = "매치 상태", example = "PENDING / MATCHED / COMPLETED")
    val status: MatchStatus?,
    @field:Schema(description = "정렬 기준 (created / scheduledAt)", example = "created asc")
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

@Schema(description = "매치 참가 RequestDTO")
data class JoinMatchRequest(
    @field:Schema(description = "Club ID")
    val clubId: String,
    @field:Schema(description = "Player ID")
    val playerId: String,
    @field:Schema(description = "Player 이름")
    val playerName: String
)

fun JoinMatchRequest.toCommand(matchId: String): JoinMatchCommand {
    return JoinMatchCommand(
        matchId = matchId,
        clubId = this.clubId,
        playerId = this.playerId,
        playerName = this.playerName
    )
}

@Schema(description = "매치 참가 취소 RequestDTO")
data class CancelMatchRequest(
    @field:Schema(description = "Club ID")
    val clubId: String,
    @field:Schema(description = "Player ID")
    val playerId: String
) {
    fun toCommand(matchId: String): CancelMatchCommand {
        return CancelMatchCommand(
            matchId = matchId,
            clubId = this.clubId,
            playerId = this.playerId,
        )
    }
}
package com.orca.match.api

import com.orca.match.domain.TeamType
import com.orca.match.domain.toResponse
import com.orca.match.service.MatchService
import com.orca.match.util.baseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Match", description = "Match APIs")
@RequestMapping
@RestController
class MatchController(
    private val matchService: MatchService
) {
    @Operation(
        summary = "매칭 등록",
        description = "새로운 매치를 등록합니다."
    )
    @PostMapping
    suspend fun create(@RequestBody request: MatchCreateRequest): ResponseEntity<MatchResponse> {
        return baseResponse(
            body = matchService.create(request.toCommand()).toResponse()
        )
    }

    @Operation(
        summary = "매치 단건 조회",
        description = "matchId를 통해 등록된 매치 정보를 조회합니다."
    )
    @GetMapping("/{matchId}")
    suspend fun getMatch(@PathVariable matchId: String): ResponseEntity<MatchResponse> {
        return baseResponse(
            body = matchService.getMatch(ObjectId(matchId)).toResponse()
        )
    }

    @Operation(
        summary = "매치 목록 조회",
        description = "등록된 매치 정보 전체 목록 조회"
    )
    @GetMapping
    suspend fun getMatches(
        @Valid criteria: MatchCriteria
    ): ResponseEntity<List<MatchResponse>> {
        return baseResponse(
            body = matchService.getMatches(criteria.toQuery()).map { it.toResponse() }
        )
    }

    @Operation(
        summary = "매치 목록 조회 by Club ID",
        description = "클럽 ID를 기준으로 등록된 매치 정보를 조회"
    )
    @GetMapping("/by-club/{clubId}")
    suspend fun getClubMatches(
        @PathVariable clubId: String,
        @Valid criteria: MatchCriteria
    ): ResponseEntity<List<MatchResponse>> {
        return baseResponse(
            body = matchService.getMatchesByClubId(ObjectId(clubId), criteria.toQuery()).map { it.toResponse() }
        )
    }

    @Operation(
        summary = "매치 참가",
        description = "매치 참가"
    )
    @PostMapping("/{matchId}/join")
    suspend fun joinMatch(
        @PathVariable matchId: String,
        @RequestBody request: JoinMatchRequest
    ): ResponseEntity<MatchRecordResponse> {
        return baseResponse(
            body = matchService.join(request.toCommand(matchId)).toResponse()
        )
    }

    @Operation(
        summary = "매치 참가 취소",
        description = "참가중인 매치 취소"
    )
    @DeleteMapping("/{matchId}/cancel")
    suspend fun cancelMatch(
        @PathVariable matchId: String,
        @RequestBody request: CancelMatchRequest
    ): ResponseEntity<Void> {
        matchService.cancel(request.toCommand(matchId))
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "매치 참가 정보 조회 by matchId",
        description = "매칭된 양 클럽 별 매치 상세 정보 (MatchRecord) 조회"
    )
    @GetMapping("/{matchId}/records")
    suspend fun getMatchRecords(
        @PathVariable matchId: String,
        @RequestParam teamType: TeamType? = null
    ): ResponseEntity<List<MatchRecordResponse>> {
        return baseResponse(
            body = matchService.getMatchRecords(ObjectId(matchId), teamType).map { it.toResponse() }
        )
    }
}
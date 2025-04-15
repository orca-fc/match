package com.orca.match.api

import com.orca.match.domain.toResponse
import com.orca.match.service.MatchService
import com.orca.match.util.baseResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping
@RestController
class MatchController(
    private val matchService: MatchService
) {
    @PostMapping
    suspend fun create(@RequestBody request: MatchCreateRequest): ResponseEntity<MatchResponse> {
        return baseResponse(
            body = matchService.create(request.toCommand()).toResponse()
        )
    }

    @GetMapping("/{matchId}")
    suspend fun getMatch(@PathVariable matchId: String): ResponseEntity<MatchResponse> {
        return baseResponse(
            body = matchService.getMatch(matchId).toResponse()
        )
    }

    @GetMapping
    suspend fun getMatches(
        @Valid criteria: MatchCriteria
    ): ResponseEntity<List<MatchResponse>> {
        return baseResponse(
            body = matchService.getMatches(criteria.toQuery()).map { it.toResponse() }
        )
    }

    @GetMapping("/by-club/{clubId}")
    suspend fun getClubMatches(
        @PathVariable clubId: String,
        @Valid criteria: MatchCriteria
    ): ResponseEntity<List<MatchResponse>> {
        return baseResponse(
            body = matchService.getMatchesByClubId(clubId, criteria.toQuery()).map { it.toResponse() }
        )
    }

    @PostMapping("/{matchId}/join")
    suspend fun joinMatch(
        @PathVariable matchId: String,
        @RequestBody request: JoinMatchRequest
    ) {
        matchService.join(request.toCommand())
    }

    // TODO 참가 취소
}
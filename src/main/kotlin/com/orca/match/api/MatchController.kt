package com.orca.match.api

import com.orca.match.domain.toResponse
import com.orca.match.service.MatchService
import com.orca.match.util.baseResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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
}
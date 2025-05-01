package com.orca.match.api

import com.orca.match.domain.ApplicationStatus
import com.orca.match.domain.toResponse
import com.orca.match.service.application.MatchApplicationService
import com.orca.match.util.baseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "MatchApplication", description = "MatchApplication APIs")
@RequestMapping
@RestController
class MatchApplicationController(
    private val matchApplicationService: MatchApplicationService
) {

    @Operation(
        summary = "매칭 신청",
        description = "클럽 매칭 신청"
    )
    @PostMapping("/{matchId}/apply")
    suspend fun applyForMatch(
        @PathVariable matchId: String,
        @RequestBody request: ApplyMatchRequest
    ): ResponseEntity<MatchApplicationResponse> {
        return baseResponse(
            body = matchApplicationService.apply(request.toCommand(matchId)).toResponse()
        )
    }

    @Operation(
        summary = "매칭 요청 목록 조회",
        description = "요청된 매칭 신청 목록을 조회하는 API"
    )
    @GetMapping("/{matchId}/applications")
    suspend fun getMatchApplications(
        @PathVariable matchId: String,
        @RequestParam status: ApplicationStatus? = null
    ): ResponseEntity<List<MatchApplicationResponse>> {
        return baseResponse(
            body = matchApplicationService.getAllApplications(ObjectId(matchId), status).map { it.toResponse() }
        )
    }

    @Operation(
        summary = "매칭 신청 수락",
        description = "매칭 신청 수락 API"
    )
    @PostMapping("/applications/{matchApplicationId}/accept")
    suspend fun acceptMatchApplication(
        @PathVariable matchApplicationId: String
    ): ResponseEntity<MatchApplicationResponse> {
        return baseResponse(
            body = matchApplicationService.respondApplication(ObjectId(matchApplicationId), ApplicationStatus.ACCEPTED)
                .toResponse()
        )
    }

    @Operation(
        summary = "매칭 신청 거절",
        description = "매칭 신청 거절 API"
    )
    @PostMapping("/applications/{matchApplicationId}/reject")
    suspend fun rejectMatchApplication(
        @PathVariable matchApplicationId: String
    ): ResponseEntity<MatchApplicationResponse> {
        return baseResponse(
            body = matchApplicationService.respondApplication(ObjectId(matchApplicationId), ApplicationStatus.REJECTED)
                .toResponse()
        )
    }
}
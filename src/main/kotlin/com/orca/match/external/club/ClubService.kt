package com.orca.match.external.club

import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.external.config.WebClientFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBodilessEntity

@Service
class ClubService(
    clientFactory: WebClientFactory
) {
    private val client = clientFactory.getClient("club")

    suspend fun getClub(clubId: String) {
        try {
            client.get()
                .uri("/{clubId}", clubId)
                .retrieve()
                .awaitBodilessEntity()
        } catch (e: WebClientResponseException) {
            throw BaseException(ErrorCode.CLUB_NOT_FOUND)
        }
    }
}
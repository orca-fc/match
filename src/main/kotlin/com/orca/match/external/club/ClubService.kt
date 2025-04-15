package com.orca.match.external.club

import com.orca.match.exception.ErrorCode
import com.orca.match.exception.ExternalServiceException
import com.orca.match.external.config.WebClientFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

@Service
class ClubService(
    clientFactory: WebClientFactory
) {
    private val client = clientFactory.getClient("club")
    private val serviceName: String = "club"

    suspend fun getClub(clubId: String): ClubResponse {
        return try {
            client.get()
                .uri("/{clubId}", clubId)
                .retrieve()
                .awaitBody<ClubResponse>()
        } catch (e: WebClientResponseException) {
            throw ExternalServiceException(
                errorCode = when (e.statusCode) {
                    HttpStatus.BAD_REQUEST -> ErrorCode.BAD_REQUEST
                    HttpStatus.NOT_FOUND -> ErrorCode.CLUB_NOT_FOUND
                    else -> ErrorCode.EXTERNAL_SERVICE_EXCEPTION
                },
                serviceName = serviceName,
                e = e
            )
        } catch (e: Exception) {
            throw ExternalServiceException(
                errorCode = ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE,
                serviceName = serviceName,
                e = e
            )
        }
    }

    suspend fun getPlayer(clubId: String, playerId: String): PlayerResponse {
        return try {
            client.get()
                .uri("/{clubId}/players/{playerId}", clubId, playerId)
                .retrieve()
                .awaitBody<PlayerResponse>()
        } catch (e: WebClientResponseException) {
            throw ExternalServiceException(
                errorCode = when (e.statusCode) {
                    HttpStatus.BAD_REQUEST -> ErrorCode.BAD_REQUEST
                    HttpStatus.NOT_FOUND -> ErrorCode.PLAYER_NOT_FOUND
                    else -> ErrorCode.EXTERNAL_SERVICE_EXCEPTION
                },
                serviceName = serviceName,
                e = e
            )
        } catch (e: Exception) {
            throw ExternalServiceException(
                errorCode = ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE,
                serviceName = serviceName,
                e = e
            )
        }
    }
}
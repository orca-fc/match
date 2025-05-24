package com.orca.match.service.application

import com.orca.match.domain.ApplicationStatus
import com.orca.match.domain.MatchApplication
import com.orca.match.domain.TeamType
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.service.ApplyMatchCommand
import com.orca.match.service.MatchService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class MatchApplicationService(
    private val matchApplicationManager: MatchApplicationManager,
    private val matchApplicationReader: MatchApplicationReader,
    private val matchService: MatchService
) {
    suspend fun apply(command: ApplyMatchCommand): MatchApplication {
        validateDuplicatedMatchApplication(command.matchId, command.clubId)
        return matchApplicationManager.create(command.matchId, command.clubId)
            ?: throw BaseException(ErrorCode.MATCH_APPLICATION_CREATE_FAILED)
    }

    private suspend fun validateDuplicatedMatchApplication(matchId: ObjectId, clubId: ObjectId) {
        if (matchApplicationReader.findByMatchIdAndClubId(
                matchId,
                clubId,
                listOf(ApplicationStatus.PENDING, ApplicationStatus.ACCEPTED)
            ) != null
        ) {
            throw BaseException(ErrorCode.DUPLICATE_MATCH_APPLICATION)
        }
    }

    suspend fun get(matchApplicationId: ObjectId): MatchApplication {
        return matchApplicationReader.findOneById(matchApplicationId)
            ?: throw BaseException(ErrorCode.MATCH_APPLICATION_NOT_FOUND)
    }

    suspend fun getAllApplications(matchId: ObjectId, status: ApplicationStatus?): List<MatchApplication> {
        return matchApplicationReader.findAllByMatchId(matchId, status)
    }

    suspend fun respondApplication(matchApplicationId: ObjectId, status: ApplicationStatus): MatchApplication {
        val application = get(matchApplicationId)
        return coroutineScope {
            try {
                val updateApplicationStatus = async { matchApplicationManager.updateStatus(matchApplicationId, status) }
                if (ApplicationStatus.ACCEPTED == status) {
                    launch { matchService.registerToMatch(application.matchId, application.clubId, TeamType.AWAY) }
                    launch { matchService.cofirmMatch(application.matchId) }
                }

                updateApplicationStatus.await()
                application
            } catch (e: Exception) {
                launch { revertToPending(matchApplicationId) }

                if (ApplicationStatus.ACCEPTED == status) {
                    launch { matchService.cancelFromMatch(application.matchId, application.clubId) }
                    launch { matchService.revertToPendingStatus(application.matchId) }
                }

                throw BaseException(ErrorCode.MATCH_RESPOND_FAILED)
            }
        }
    }

    suspend fun revertToPending(matchApplicationId: ObjectId) {
        matchApplicationManager.updateStatus(matchApplicationId, ApplicationStatus.PENDING)
    }
}
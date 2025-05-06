package com.orca.match.service

import com.orca.match.domain.*
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.external.club.ClubService
import com.orca.match.service.match.MatchManager
import com.orca.match.service.match.MatchReader
import com.orca.match.service.record.MatchRecordManager
import com.orca.match.service.record.MatchRecordReader
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchManager: MatchManager,
    private val matchReader: MatchReader,
    private val matchRecordReader: MatchRecordReader,
    private val matchRecordManager: MatchRecordManager,
    private val clubService: ClubService
) {
    suspend fun create(command: CreateMatchCommand): Match {
        validateClubId(command.clubId)
        val match = matchManager.createMatch(command)
        try {
            return registerToMatch(match.id!!, command.clubId, TeamType.HOME)
        } catch (e: Exception) {
            matchManager.deleteMatch(match.id!!)
            throw BaseException(ErrorCode.MATCH_CREATE_FAILED)
        }
    }

    private suspend fun validateClubId(clubId: ObjectId) {
        clubService.getClub(clubId)
    }

    suspend fun getMatch(matchId: ObjectId): Match {
        return matchReader.findOneById(matchId) ?: throw BaseException(ErrorCode.MATCH_NOT_FOUND)
    }

    suspend fun getMatches(query: MatchQuery): List<Match> {
        return matchReader.findAll(query)
    }

    suspend fun getMatchesByClubId(clubId: ObjectId, query: MatchQuery): List<Match> {
        return matchReader.findAllByClubId(clubId, query)
    }

    suspend fun joinMatch(command: JoinMatchCommand): MatchRecord {
        validateIsClubMember(command.clubId, command.playerId)

        val matchRecord = matchRecordReader.findOneByMatchIdAndClubId(
            matchId = command.matchId,
            clubId = command.clubId
        ) ?: throw BaseException(ErrorCode.MATCH_RECORD_NOT_FOUND)

        return matchManager.joinMatch(matchRecordId = matchRecord.id!!, command = command)
    }

    suspend fun withdrawFromMatch(command: CancelMatchCommand) {
        validateIsClubMember(command.clubId, command.playerId)
        val matchRecord = matchRecordReader.findOneByMatchIdAndClubId(
            matchId = command.matchId,
            clubId = command.clubId
        ) ?: throw BaseException(ErrorCode.MATCH_RECORD_NOT_FOUND)

        return matchRecordManager.deletePlayerRecords(matchRecordId = matchRecord.id!!, playerId = command.playerId)
    }

    suspend fun validateIsClubMember(clubId: ObjectId, playerId: ObjectId) {
        clubService.getPlayer(clubId, playerId)
    }

    suspend fun getMatchRecordByMatchId(matchId: ObjectId, teamType: TeamType? = null): List<MatchRecord> {
        val matchRecords = matchRecordReader.findRecordsByMatchId(matchId, teamType)
        if (matchRecords.isEmpty()) throw BaseException(ErrorCode.MATCH_NOT_FOUND)

        return matchRecords
    }

    suspend fun registerToMatch(matchId: ObjectId, clubId: ObjectId, teamType: TeamType): Match {
        val record = matchRecordManager.create(
            matchId = matchId,
            clubId = clubId,
            teamType = teamType
        )
        return matchManager.addMatchRecord(matchId, record.id!!)
    }

    suspend fun cancelFromMatch(matchId: ObjectId, clubId: ObjectId) {
        val matchRecord = matchRecordReader.findOneByMatchIdAndClubId(matchId, clubId)

        if (matchRecord != null) {
            matchRecordManager.delete(matchRecord.id!!)
            matchManager.deleteRecord(matchId, matchRecord.id)
        }
    }

    suspend fun cofirmMatch(matchId: ObjectId): Match {
        return matchManager.updateMatchStatus(matchId, MatchStatus.MATCHED)
            ?: throw BaseException(ErrorCode.MATCH_NOT_FOUND)
    }

    suspend fun revertToPendingStatus(matchId: ObjectId): Match {
        return matchManager.updateMatchStatus(matchId, MatchStatus.PENDING)
            ?: throw BaseException(ErrorCode.MATCH_NOT_FOUND)
    }

    suspend fun registerMatchRecords(command: RegisterMatchRecordsCommand): MatchRecord {
        return coroutineScope {
            launch {
                matchRecordManager.updateMatchRecord(
                    command.matchRecordId,
                    command.resultType,
                    command.opponentMannerScore
                )
            }

            val matchRecordDeferred = async { matchRecordManager.updatePlayerRecords(command.matchRecordId, command.playerRecords) }
            val updatedRecord = matchRecordDeferred.await()

            val matchRecordsMap = getMatchRecordByMatchId(updatedRecord.matchId).associateBy { it.teamType }

            val matchCompleted = matchRecordsMap.values.all { it.resultType != null }

            if (matchCompleted) {
                val homeResult = matchRecordsMap[TeamType.HOME]!!.resultType
                val awayResult = matchRecordsMap[TeamType.AWAY]!!.resultType

                val validateResults = when (homeResult) {
                    ResultType.DRAW -> {
                        ResultType.DRAW == awayResult
                    }
                    else -> {
                        homeResult != awayResult
                    }
                }

                if (validateResults) {
                    matchManager.updateMatchStatus(updatedRecord.matchId, MatchStatus.COMPLETED)
                } else {
                    throw BaseException(ErrorCode.INVALID_MATCH_RESULTS)
                }
            }
            updatedRecord
        }

    }
}
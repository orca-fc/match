package com.orca.match.service

import com.orca.match.domain.Match
import com.orca.match.domain.MatchRecord
import com.orca.match.domain.TeamType
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.external.club.ClubService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchManager: MatchManager,
    private val matchReader: MatchReader,
    private val matchRecordReader: MatchRecordReader,
    private val clubService: ClubService
) {
    suspend fun create(command: CreateMatchCommand): Match {
        validateClubId(ObjectId(command.clubId))
        val match = matchManager.createMatch(command)
        try {
            val homeRecord = matchManager.createMatchRecord(
                matchId = match.id!!,
                clubId = ObjectId(command.clubId),
                teamType = TeamType.HOME
            )
            return matchManager.addMatchRecord(matchId = match.id, matchRecordId = homeRecord.id!!)
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

    suspend fun join(command: JoinMatchCommand): MatchRecord {
        validateIsClubMember(ObjectId(command.clubId), ObjectId(command.playerId))

        val matchRecord = matchRecordReader.findOneByMatchIdAndClubId(
            matchId = ObjectId(command.matchId),
            clubId = ObjectId(command.clubId)
        ) ?: throw BaseException(ErrorCode.MATCH_RECORD_NOT_FOUND)

        return matchManager.joinMatch(matchRecordId = matchRecord.id!!, command = command)
    }

    suspend fun cancel(command: CancelMatchCommand) {
        validateIsClubMember(ObjectId(command.clubId), ObjectId(command.playerId))
        val matchRecord = matchRecordReader.findOneByMatchIdAndClubId(
            matchId = ObjectId(command.matchId),
            clubId = ObjectId(command.clubId)
        ) ?: throw BaseException(ErrorCode.MATCH_RECORD_NOT_FOUND)

        return matchManager.cancelMatch(matchRecordId = matchRecord.id!!, playerId = ObjectId(command.playerId))
    }

    suspend fun validateIsClubMember(clubId: ObjectId, playerId: ObjectId) {
        clubService.getPlayer(clubId, playerId)
    }

    suspend fun getMatchRecords(matchId: ObjectId, teamType: TeamType?): List<MatchRecord> {
        val matchRecords = matchRecordReader.findRecordsByMatchId(matchId, teamType)
        if (matchRecords.isEmpty()) throw BaseException(ErrorCode.MATCH_NOT_FOUND)

        return matchRecords
    }
}
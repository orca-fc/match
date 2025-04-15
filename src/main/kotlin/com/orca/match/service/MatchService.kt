package com.orca.match.service

import com.orca.match.domain.Match
import com.orca.match.domain.MatchRecord
import com.orca.match.domain.TeamType
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.external.club.ClubService
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchManager: MatchManager,
    private val matchReader: MatchReader,
    private val matchRecordReader: MatchRecordReader,
    private val clubService: ClubService
) {
    suspend fun create(command: CreateMatchCommand): Match {
        validateClubId(command.clubId)

        val match = matchManager.createMatch(command)
        val homeRecord = matchManager.createMatchRecord(
            matchId = match.id!!,
            clubId = command.clubId,
            teamType = TeamType.HOME
        )

        return matchManager.addMatchRecord(matchId = match.id, matchRecordId = homeRecord.id!!)
    }

    suspend fun validateClubId(clubId: String) {
        clubService.getClub(clubId)
    }

    suspend fun getMatch(matchId: String): Match {
        return matchReader.findOneById(matchId) ?: throw BaseException(ErrorCode.MATCH_NOT_FOUND)
    }

    suspend fun getMatches(query: MatchQuery): List<Match> {
        return matchReader.findAll(query)
    }

    suspend fun getMatchesByClubId(clubId: String, query: MatchQuery): List<Match> {
        return matchReader.findAllByClubId(clubId, query)
    }

    suspend fun join(command: JoinMatchCommand): MatchRecord {
        val matchRecord = matchRecordReader.findOneByMatchIdAndClubId(
            matchId = command.matchId,
            clubId = command.clubId
        ) ?: throw BaseException(ErrorCode.MATCH_RECORD_NOT_FOUND)

        return matchManager.joinMatch(matchRecordId = matchRecord.id!!, command = command)
    }
}
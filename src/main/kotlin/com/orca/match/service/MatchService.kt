package com.orca.match.service

import com.orca.match.domain.Match
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.external.club.ClubService
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchManager: MatchManager,
    private val matchReader: MatchReader,
    private val clubService: ClubService
) {
    suspend fun create(command: MatchCreateCommand): Match {
        validateClubId(command.clubId)
        return matchManager.create(command)
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
}
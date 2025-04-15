package com.orca.match.service

import com.orca.match.domain.MatchRecord
import com.orca.match.repository.MatchRecordRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Repository

@Repository
class MatchRecordReader(
    private val matchRecordRepository: MatchRecordRepository
) {
    suspend fun findOneByMatchIdAndClubId(matchId: String, clubId: String): MatchRecord? {
        return matchRecordRepository.findByMatchIdAndClubId(matchId, clubId).awaitSingleOrNull()
    }
}
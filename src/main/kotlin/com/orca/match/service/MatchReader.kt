package com.orca.match.service

import com.orca.match.domain.Match
import com.orca.match.repository.MatchRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Repository

@Repository
class MatchReader(
    private val matchRepository: MatchRepository
) {
    suspend fun findOneById(matchId: String): Match? {
        return matchRepository.findById(matchId).awaitSingleOrNull()
    }
}
package com.orca.match.service

import com.orca.match.domain.Match
import com.orca.match.domain.MatchResult
import com.orca.match.repository.MatchRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Repository

@Repository
class MatchManager(
    private val matchRepository: MatchRepository
) {
    suspend fun create(command: MatchCreateCommand): Match {
        return matchRepository.save(
            Match(
                scheduledAt = command.scheduledAt,
                venue = command.venue,
                cost = command.cost,
                content = command.content,
                home = MatchResult(command.clubId)
            )
        ).awaitSingle()
    }
}
package com.orca.match.service

import com.orca.match.domain.Match
import com.orca.match.domain.MatchRecord
import com.orca.match.domain.PlayerRecord
import com.orca.match.domain.TeamType
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.repository.MatchRecordRepository
import com.orca.match.repository.MatchRepository
import com.orca.match.util.buildQueryById
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class MatchManager(
    private val matchRepository: MatchRepository,
    private val matchRecordRepository: MatchRecordRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun createMatch(command: CreateMatchCommand): Match {
        return matchRepository.save(
            Match(
                scheduledAt = command.scheduledAt,
                venue = command.venue,
                cost = command.cost,
                content = command.content
            )
        ).awaitSingle()
    }

    suspend fun createMatchRecord(matchId: String, clubId: String, teamType: TeamType): MatchRecord {
        return matchRecordRepository.save(
            MatchRecord(
                matchId = matchId,
                clubId = clubId,
                teamType = teamType
            )
        ).awaitSingle()
    }

    suspend fun addMatchRecord(matchId: String, matchRecordId: String): Match {
        val update = Update().apply {
            push("records", matchRecordId)
        }
        return reactiveMongoTemplate.findAndModify(
            buildQueryById(matchId), update, Match::class.java
        ).awaitSingle()
    }

    suspend fun joinMatch(matchRecordId: String, command: JoinMatchCommand): MatchRecord {
        val update = Update().apply {
            addToSet("records", PlayerRecord(id = command.playerId, name = command.playerName))
        }

        val result = reactiveMongoTemplate.updateFirst(
            buildQueryById(matchRecordId),
            update,
            MatchRecord::class.java
        ).awaitSingle()

        return if (result.matchedCount == 0L) {
            throw BaseException(ErrorCode.MATCH_RECORD_NOT_FOUND)
        } else if (result.modifiedCount == 0L) {
            throw BaseException(ErrorCode.PARTICIPANT_DUPLICATED)
        } else {
            matchRecordRepository.findById(matchRecordId).awaitSingle()
        }
    }
}
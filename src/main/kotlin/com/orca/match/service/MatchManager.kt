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
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.Document
import org.bson.types.ObjectId
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

    suspend fun deleteMatch(matchId: ObjectId) {
        matchRepository.deleteById(matchId).awaitSingleOrNull()
    }

    suspend fun createMatchRecord(matchId: ObjectId, clubId: ObjectId, teamType: TeamType): MatchRecord {
        return matchRecordRepository.save(
            MatchRecord(
                matchId = matchId,
                clubId = clubId,
                teamType = teamType
            )
        ).awaitSingle()
    }

    suspend fun deleteMatchRecord(matchRecordId: ObjectId) {
        matchRecordRepository.deleteById(matchRecordId).awaitSingleOrNull()
    }

    suspend fun addMatchRecord(matchId: ObjectId, matchRecordId: ObjectId): Match {
        val update = Update().apply {
            push("records", matchRecordId)
        }
        return reactiveMongoTemplate.findAndModify(
            buildQueryById(matchId), update, Match::class.java
        ).awaitSingle()
    }

    suspend fun joinMatch(matchRecordId: ObjectId, command: JoinMatchCommand): MatchRecord {
        val update = Update().apply {
            addToSet("records", PlayerRecord(id = ObjectId(command.playerId), name = command.playerName))
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

    suspend fun cancelMatch(matchRecordId: ObjectId, playerId: ObjectId) {
        val update = Update().apply {
            pull("records", Document("id", playerId))
        }

        val result = reactiveMongoTemplate.updateFirst(
            buildQueryById(matchRecordId),
            update,
            MatchRecord::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L) {
            throw BaseException(ErrorCode.MATCH_RECORD_NOT_FOUND)
        } else if (result.modifiedCount == 0L) {
            throw BaseException(ErrorCode.PLAYER_NOT_IN_MATCH)
        }
    }
}
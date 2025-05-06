package com.orca.match.service.match

import com.orca.match.domain.Match
import com.orca.match.domain.MatchRecord
import com.orca.match.domain.MatchStatus
import com.orca.match.domain.PlayerRecord
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.repository.MatchRepository
import com.orca.match.service.CreateMatchCommand
import com.orca.match.service.JoinMatchCommand
import com.orca.match.service.record.MatchRecordReader
import com.orca.match.util.buildQueryById
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class MatchManager(
    private val matchRepository: MatchRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val matchRecordReader: MatchRecordReader
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

    suspend fun addMatchRecord(matchId: ObjectId, matchRecordId: ObjectId): Match {
        val update = Update().apply {
            push("records", matchRecordId)
        }
        return reactiveMongoTemplate.findAndModify(
            buildQueryById(matchId), update, Match::class.java
        ).awaitSingle()
    }

    suspend fun deleteRecord(matchId: ObjectId, recordId: ObjectId) {
        val update = Update().apply {
            pull("records", recordId)
        }

        reactiveMongoTemplate.updateFirst(
            buildQueryById(matchId),
            update,
            Match::class.java
        ).awaitSingle()
    }

    suspend fun joinMatch(matchRecordId: ObjectId, command: JoinMatchCommand): MatchRecord {
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
            matchRecordReader.findOneById(matchRecordId)!!
        }
    }

    suspend fun updateMatchStatus(matchId: ObjectId, status: MatchStatus): Match? {
        val update = Update().apply {
            set("status", status)
        }

        return reactiveMongoTemplate.findAndModify(
            buildQueryById(matchId),
            update,
            FindAndModifyOptions().returnNew(true),
            Match::class.java
        ).awaitSingleOrNull()
    }
}
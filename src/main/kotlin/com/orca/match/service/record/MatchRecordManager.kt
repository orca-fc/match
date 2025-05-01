package com.orca.match.service.record

import com.orca.match.domain.MatchRecord
import com.orca.match.domain.TeamType
import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import com.orca.match.repository.MatchRecordRepository
import com.orca.match.util.buildQueryById
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class MatchRecordManager(
    private val matchRecordRepository: MatchRecordRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun create(matchId: ObjectId, clubId: ObjectId, teamType: TeamType): MatchRecord {
        return matchRecordRepository.save(
            MatchRecord(
                matchId = matchId,
                clubId = clubId,
                teamType = teamType
            )
        ).awaitSingle()
    }

    suspend fun delete(matchRecordId: ObjectId) {
        matchRecordRepository.deleteById(matchRecordId).awaitSingleOrNull()
    }

    suspend fun deletePlayerRecords(matchRecordId: ObjectId, playerId: ObjectId) {
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
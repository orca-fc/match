package com.orca.match.service.record

import com.orca.match.domain.MatchRecord
import com.orca.match.domain.TeamType
import com.orca.match.repository.MatchRecordRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class MatchRecordReader(
    private val matchRecordRepository: MatchRecordRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun findOneById(matchRecordId: ObjectId): MatchRecord? {
        return matchRecordRepository.findById(matchRecordId).awaitSingleOrNull()
    }

    suspend fun findOneByMatchIdAndClubId(matchId: ObjectId, clubId: ObjectId): MatchRecord? {
        return matchRecordRepository.findByMatchIdAndClubId(matchId, clubId).awaitSingleOrNull()
    }

    suspend fun findRecordsByMatchId(matchId: ObjectId, teamType: TeamType?): List<MatchRecord> {
        val query = Query().apply {
            addCriteria(Criteria.where("matchId").`is`(matchId))
            teamType?.let { addCriteria(Criteria.where("teamType").`is`(teamType)) }
        }

        return reactiveMongoTemplate.find(
            query,
            MatchRecord::class.java
        ).collectList().awaitSingle().toList()
    }
}
package com.orca.match.service.application

import com.orca.match.domain.ApplicationStatus
import com.orca.match.domain.MatchApplication
import com.orca.match.repository.MatchApplicationRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class MatchApplicationReader(
    private val matchApplicationRepository: MatchApplicationRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun findOneById(matchApplicationId: ObjectId): MatchApplication? {
        return matchApplicationRepository.findById(matchApplicationId).awaitSingleOrNull()
    }

    suspend fun findByMatchIdAndClubId(
        matchId: ObjectId,
        clubId: ObjectId,
        statuses: List<ApplicationStatus>?
    ): MatchApplication? {
        val query = Query().apply {
            addCriteria(Criteria.where("matchId").`is`(matchId).and("clubId").`is`(clubId))
            statuses?.let { addCriteria(Criteria.where("status").`in`(it)) }
        }

        return reactiveMongoTemplate.findOne(
            query,
            MatchApplication::class.java
        ).awaitSingleOrNull()
    }

    suspend fun findAllByMatchId(matchId: ObjectId, status: ApplicationStatus?): List<MatchApplication> {
        val query = Query().apply {
            addCriteria(Criteria.where("matchId").`is`(matchId))
            status?.let { addCriteria(Criteria.where("status").`is`(it)) }
        }

        return reactiveMongoTemplate.find(
            query,
            MatchApplication::class.java
        ).collectList().awaitSingle().toList()
    }
}
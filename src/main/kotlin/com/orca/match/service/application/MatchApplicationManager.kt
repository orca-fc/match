package com.orca.match.service.application

import com.orca.match.domain.ApplicationStatus
import com.orca.match.domain.MatchApplication
import com.orca.match.repository.MatchApplicationRepository
import com.orca.match.util.buildQueryById
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class MatchApplicationManager(
    private val matchApplicationRepository: MatchApplicationRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun create(matchId: ObjectId, clubId: ObjectId): MatchApplication? {
        return matchApplicationRepository.save(
            MatchApplication(
                matchId = matchId,
                clubId = clubId
            )
        ).awaitSingleOrNull()
    }

    suspend fun updateStatus(matchApplicationId: ObjectId, status: ApplicationStatus): MatchApplication? {
        val update = Update().apply {
            set("status", status)
        }

        return reactiveMongoTemplate.findAndModify(
            buildQueryById(matchApplicationId),
            update,
            FindAndModifyOptions().returnNew(true),
            MatchApplication::class.java
        ).awaitSingleOrNull()
    }
}
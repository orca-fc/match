package com.orca.match.repository

import com.orca.match.domain.MatchRecord
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MatchRecordRepository: ReactiveMongoRepository<MatchRecord, ObjectId> {
    fun findByMatchIdAndClubId(matchId: ObjectId, clubId: ObjectId): Mono<MatchRecord>
}
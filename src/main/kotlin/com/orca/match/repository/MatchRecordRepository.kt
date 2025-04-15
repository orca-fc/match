package com.orca.match.repository

import com.orca.match.domain.MatchRecord
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MatchRecordRepository: ReactiveMongoRepository<MatchRecord, String> {
    fun findByMatchIdAndClubId(matchId: String, clubId: String): Mono<MatchRecord>
}
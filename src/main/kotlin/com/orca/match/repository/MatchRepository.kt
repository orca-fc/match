package com.orca.match.repository

import com.orca.match.domain.Match
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface MatchRepository: ReactiveMongoRepository<Match, ObjectId> {
}
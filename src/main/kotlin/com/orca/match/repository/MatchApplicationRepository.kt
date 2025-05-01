package com.orca.match.repository

import com.orca.match.domain.MatchApplication
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface MatchApplicationRepository: ReactiveMongoRepository<MatchApplication, ObjectId> {
}
package com.orca.match.repository

import com.orca.match.domain.Match
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface MatchRepository: ReactiveMongoRepository<Match, String> {
}
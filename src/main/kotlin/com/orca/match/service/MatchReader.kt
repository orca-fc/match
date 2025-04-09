package com.orca.match.service

import com.orca.match.domain.Match
import com.orca.match.repository.MatchRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class MatchReader(
    private val matchRepository: MatchRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun findOneById(matchId: String): Match? {
        return matchRepository.findById(matchId).awaitSingleOrNull()
    }

    suspend fun findAll(options: MatchQuery): List<Match> {
        return reactiveMongoTemplate.find(
            Query().apply { applyQueryOptions(options) },
            Match::class.java
        ).collectList().awaitSingle()
    }

    suspend fun findAllByClubId(clubId: String, options: MatchQuery): List<Match> {
        val homeCriteria = Criteria.where("home.clubId").`is`(clubId)
        val awayCriteria = Criteria.where("away.clubId").`is`(clubId)

        val query = Query().apply {
            addCriteria(Criteria().orOperator(homeCriteria, awayCriteria))
            applyQueryOptions(options)
        }


        return reactiveMongoTemplate.find(
            query, Match::class.java
        ).collectList().awaitSingle()
    }

    private fun Query.applyQueryOptions(options: MatchQuery) {
        options.status?.let { addCriteria(Criteria.where("status").`is`(it)) }
        val sortOrders = if (options.sortOptions.isNotEmpty()) {
            options.sortOptions.map { (field, direction) ->
                Sort.Order(direction, field)
            }
        } else {
            listOf(Sort.Order(Sort.Direction.DESC, "createdAt"))
        }
        with(Sort.by(sortOrders))
    }

    suspend fun getQueryById(id: String): Query {
        return Query(Criteria.where("_id").`is`(id))
    }
}
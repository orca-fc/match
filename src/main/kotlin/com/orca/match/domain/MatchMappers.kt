package com.orca.match.domain

import com.orca.match.api.MatchResponse
import com.orca.match.api.MatchResultResponse
import com.orca.match.api.PlayerRecordResponse
import com.orca.match.util.toKstString

fun Match.toResponse(): MatchResponse {
    return MatchResponse(
        id = this.id!!,
        scheduledAt = this.scheduledAt.toKstString(),
        venue = this.venue,
        cost = this.cost,
        content = this.content,
        status = this.status.value,
        home = this.home.toResponse(),
        away = if (this.away == null) null else this.away.toResponse(),
        createdAt = this.createdAt,
    )
}

fun MatchResult.toResponse(): MatchResultResponse {
    return MatchResultResponse(
        clubId = this.clubId,
        records = this.records.map { it.toResponse() },
        resultType = if (this.resultType == null) null else this.resultType.value,
        mannerPoint = this.mannerPoint,
    )
}

fun PlayerRecord.toResponse(): PlayerRecordResponse {
    return PlayerRecordResponse(
        id = this.id,
        name = this.name,
        goal = this.goal,
        assist = this.assist,
    )
}
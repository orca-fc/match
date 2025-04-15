package com.orca.match.domain

import com.orca.match.api.MatchResponse
import com.orca.match.api.MatchRecordResponse
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
        records = this.records,
        createdAt = this.createdAt,
    )
}

fun MatchRecord.toResponse(): MatchRecordResponse {
    return MatchRecordResponse(
        id = this.id!!,
        clubId = this.clubId,
        matchId = this.matchId,
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
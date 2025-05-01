package com.orca.match.domain

import com.orca.match.api.MatchApplicationResponse
import com.orca.match.api.MatchResponse
import com.orca.match.api.MatchRecordResponse
import com.orca.match.api.PlayerRecordResponse
import com.orca.match.util.toKstString

fun Match.toResponse(): MatchResponse {
    return MatchResponse(
        id = this.id.toString(),
        scheduledAt = this.scheduledAt.toKstString(),
        venue = this.venue,
        cost = this.cost,
        content = this.content,
        status = this.status.value,
        records = this.records.map { it.toString() },
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!
    )
}

fun MatchRecord.toResponse(): MatchRecordResponse {
    return MatchRecordResponse(
        id = this.id.toString(),
        clubId = this.clubId.toString(),
        matchId = this.matchId.toString(),
        teamType = this.teamType.name,
        records = this.records.map { it.toResponse() },
        resultType = if (this.resultType == null) null else this.resultType.value,
        mannerPoint = this.mannerPoint,
    )
}

fun PlayerRecord.toResponse(): PlayerRecordResponse {
    return PlayerRecordResponse(
        id = this.id.toString(),
        name = this.name,
        goal = this.goal,
        assist = this.assist,
    )
}

fun MatchApplication.toResponse(): MatchApplicationResponse {
    return MatchApplicationResponse(
        id = this.id.toString(),
        matchId = this.matchId.toString(),
        clubId = this.clubId.toString(),
        status = this.status.name,
        statusMessage = this.status.value,
        createdAt = this.createdAt?.toKstString(),
        updatedAt = this.updatedAt?.toKstString()
    )
}
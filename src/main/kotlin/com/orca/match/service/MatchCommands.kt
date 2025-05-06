package com.orca.match.service

import com.orca.match.domain.ResultType
import org.bson.types.ObjectId
import java.time.Instant

data class CreateMatchCommand(
    val clubId: ObjectId,
    val scheduledAt: Instant,
    val venue: String,
    val cost: Int,
    val content: String
)

data class JoinMatchCommand(
    val matchId: ObjectId,
    val clubId: ObjectId,
    val playerId: ObjectId,
    val playerName: String
)

data class CancelMatchCommand(
    val matchId: ObjectId,
    val clubId: ObjectId,
    val playerId: ObjectId
)

data class ApplyMatchCommand(
    val matchId: ObjectId,
    val clubId: ObjectId
)

data class RegisterMatchRecordsCommand(
    val matchRecordId: ObjectId,
    val resultType: ResultType,
    val opponentMannerScore: Int,
    val playerRecords: List<PlayerRecordCommand>
)

data class PlayerRecordCommand(
    val id: ObjectId,
    val goal: Int,
    val assist: Int
)
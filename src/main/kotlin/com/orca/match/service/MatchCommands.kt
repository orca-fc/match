package com.orca.match.service

import java.time.Instant

data class CreateMatchCommand(
    val clubId: String,
    val scheduledAt: Instant,
    val venue: String,
    val cost: Int,
    val content: String
)

data class JoinMatchCommand(
    val matchId: String,
    val clubId: String,
    val playerId: String,
    val playerName: String
)
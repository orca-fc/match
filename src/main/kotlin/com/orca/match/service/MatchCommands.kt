package com.orca.match.service

import java.time.Instant

data class MatchCreateCommand(
    val clubId: String,
    val scheduledAt: Instant,
    val venue: String,
    val cost: Int,
    val content: String
)
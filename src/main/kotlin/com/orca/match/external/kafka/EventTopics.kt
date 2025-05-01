package com.orca.match.external.kafka

object EventTopics {
    // Player
    const val PLAYER_UPDATED = "player-updated"
    const val PLAYER_UPDATE_FAILED = "player-update-failed"

    // Club
    const val CLUB_CREATED = "club-created"
    const val CLUB_CREATE_FAILED = "club-create-failed"
    const val JOIN_ACCEPTED = "join-accepted"
    const val JOIN_ACCEPT_FAILED = "join-accept-failed"

    // Match
    const val MATCH_ACCEPTED = "match-accepted"
}
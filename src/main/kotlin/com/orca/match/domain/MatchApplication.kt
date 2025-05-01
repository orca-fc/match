package com.orca.match.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "match_applications")
data class MatchApplication(
    @Id
    val id: ObjectId? = null,
    val matchId: ObjectId,
    val clubId: ObjectId,
    val status: ApplicationStatus = ApplicationStatus.PENDING
): Auditable()

enum class ApplicationStatus(val value: String) {
    PENDING("수락 대기"),
    ACCEPTED("수락"),
    REJECTED("거절")
}
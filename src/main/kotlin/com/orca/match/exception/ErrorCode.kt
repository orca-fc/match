package com.orca.match.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

enum class ErrorCode(val httpStatus: HttpStatusCode = HttpStatus.NOT_FOUND, val message: String) {
    UNDEFINED_EXCEPTION(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR, message = "Sorry, undefined exception"),
    BAD_REQUEST(httpStatus = HttpStatus.BAD_REQUEST, message = "Bad request. check API documents."),

    PARSING_EXCEPTION(httpStatus = HttpStatus.BAD_REQUEST, message = "String to Date parsing failed. check API documents"),

    MATCH_NOT_FOUND(message = "Match not found."),
    MATCH_RECORD_NOT_FOUND(message = "Match record not found"),
    PARTICIPANT_DUPLICATED(httpStatus = HttpStatus.CONFLICT, message = "Already joined player"),

    // External service
    CLUB_NOT_FOUND(message = "Club not found."),
    PLAYER_NOT_FOUND(message = "Player not found."),
    EXTERNAL_SERVICE_EXCEPTION(httpStatus = HttpStatus.BAD_GATEWAY, message = "Error occurred from external service."),
    EXTERNAL_SERVICE_UNAVAILABLE(httpStatus = HttpStatus.BAD_GATEWAY, message = "External service unavailable")
}
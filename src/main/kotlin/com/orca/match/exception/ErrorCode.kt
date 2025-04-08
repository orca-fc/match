package com.orca.match.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus? = HttpStatus.NOT_FOUND, val message: String) {
    UNDEFINED_EXCEPTION(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Sorry, undefined exception"),
    BAD_REQUEST(status = HttpStatus.BAD_REQUEST, message = "Bad request. check API documents."),

    PARSING_EXCEPTION(status = HttpStatus.BAD_REQUEST, message = "String to Date parsing failed. check API documents"),

    MATCH_NOT_FOUND(message = "Match not found."),

    //external
    CLUB_NOT_FOUND(message = "Club not found"),
}
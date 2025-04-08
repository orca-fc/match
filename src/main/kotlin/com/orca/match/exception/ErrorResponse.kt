package com.orca.match.exception

class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: String,

    ) {
    var serviceName = "match"

    constructor(ex: BaseException) : this(
        code = ex.code,
        message = ex.message,
        timestamp = ex.timeStamp,
    )
}
package com.orca.match.exception

class ErrorResponse(
    val errorCode: String,
    val message: String,
    val timestamp: String
) {
    val serviceName: String = "match"

    constructor(ex: BaseException) : this(
        errorCode = ex.code,
        message = ex.message,
        timestamp = ex.timeStamp,
    )
}
package com.orca.match.exception

import com.orca.match.util.getCurrentTimestamp
import org.springframework.http.HttpStatusCode

class BaseException(
    val httpStatus: HttpStatusCode,
    val code: String,
    override val message: String,
) : RuntimeException() {

    val timeStamp = getCurrentTimestamp()

    constructor(e: ErrorCode) : this(
        httpStatus = e.httpStatus,
        code = e.name,
        message = e.message,
    )
}
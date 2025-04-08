package com.orca.match.exception

import com.orca.match.util.getCurrentTimestamp
import org.springframework.http.HttpStatus

class BaseException(
    val httpStatus: HttpStatus,
    val code: String,
    override val message: String,
) : RuntimeException() {

    val timeStamp = getCurrentTimestamp()

    constructor(e: ErrorCode) : this(
        httpStatus = e.status!!,
        code = e.name,
        message = e.message,
    )
}
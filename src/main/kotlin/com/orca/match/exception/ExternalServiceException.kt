package com.orca.match.exception

import com.orca.match.util.getCurrentTimestamp
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.WebClientResponseException

class ExternalServiceException(
    val errorCode: ErrorCode,
    val serviceName: String,
    val httpStatus: HttpStatusCode,
    override val cause: Throwable?,
    val exceptionDetails: String
) : RuntimeException() {
    val timestamp: String = getCurrentTimestamp()

    constructor(
        errorCode: ErrorCode,
        serviceName: String,
        e: WebClientResponseException
    ) : this(
        errorCode = errorCode,
        serviceName = serviceName,
        httpStatus = errorCode.httpStatus,
        cause = e,
        exceptionDetails = e.responseBodyAsString
    )

    constructor(
        errorCode: ErrorCode,
        serviceName: String,
        e: Exception
    ) : this(
        errorCode = errorCode,
        serviceName = serviceName,
        httpStatus = errorCode.httpStatus,
        cause = e.cause,
        exceptionDetails = e.message ?: "empty message from Exception"
    )
}

fun ExternalServiceException.toErrorResponse(): ErrorResponse {
    return ErrorResponse(
        errorCode = this.errorCode.name,
        message = this.errorCode.message,
        timestamp = this.timestamp,
    )
}
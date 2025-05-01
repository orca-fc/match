package com.orca.match.exception

import com.orca.match.util.baseResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    private val externalLogFormat = "-----Error occurred from {}-----\n ErrorCode: {}, HttpStatus: {}, Details: '{}'"

    @ExceptionHandler(Exception::class)
    fun undefinedException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Undefined Exception.\n$e")
        return baseResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            body = ErrorResponse(BaseException(ErrorCode.UNDEFINED_EXCEPTION))
        )
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleInputException(e: ServerWebInputException): ResponseEntity<ErrorResponse> {
        logger.error("Input Exception.\n${e.body}")

        return baseResponse(
            status = HttpStatus.BAD_REQUEST,
            body = ErrorResponse(BaseException(ErrorCode.BAD_REQUEST))
        )
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        return baseResponse(e.httpStatus, ErrorResponse(e))
    }

    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalServiceException(e: ExternalServiceException): ResponseEntity<ErrorResponse> {
        logger.error(
            externalLogFormat,
            e.serviceName,
            e.errorCode.name,
            e.httpStatus,
            e.exceptionDetails,
            e
        )

        return baseResponse(e.httpStatus, e.toErrorResponse())
    }
}
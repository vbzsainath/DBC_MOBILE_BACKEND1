package com.vbz.dbcards.exception;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.vbz.dbcards.exception.DuplicateResourceException;
import com.vbz.dbcards.exception.InvalidOtpException;
import com.vbz.dbcards.exception.TemplateNotFoundException;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.exception.UnauthorizedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 🔹 400 Bad Request — missing / invalid fields
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        logger.warn("Bad request: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST,
                "REQ_400",
                ex.getMessage(),
                request.getRequestURI());
    }

    // 🔹 403 Forbidden — caller is not permitted for this action
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {

        logger.warn("Forbidden: {}", ex.getMessage());

        return buildResponse(HttpStatus.FORBIDDEN,
                "AUTH_403",
                ex.getMessage(),
                request.getRequestURI());
    }

    // 🔹 User Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        logger.warn("User not found: {}", ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND,
                "USR_404",
                ex.getMessage(),
                request.getRequestURI());
    }

    // 🔹 Duplicate Entry
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        logger.warn("Duplicate entry: {}", ex.getMessage());

        return buildResponse(HttpStatus.CONFLICT,
                "USR_409",
                ex.getMessage(),
                request.getRequestURI());
    }

    // 🔹 Invalid OTP
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOtp(
            InvalidOtpException ex,
            HttpServletRequest request) {

        logger.warn("Invalid OTP: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST,
                "OTP_001",
                ex.getMessage(),
                request.getRequestURI());
    }

    // 🔹 Template Not Found
    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTemplate(
            TemplateNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND,
                "TPL_404",
                ex.getMessage(),
                request.getRequestURI());
    }

    // 🔹 DB Constraint
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDBException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        logger.error("Database error: ", ex);

        return buildResponse(HttpStatus.CONFLICT,
                "DB_409",
                "Duplicate entry or constraint violation",
                request.getRequestURI());
    }

    // 🔹 404 — no controller mapped for this path (e.g. /api/share/received/8)
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex,
            HttpServletRequest request) {

        logger.warn("Route not found: {}", request.getRequestURI());

        return buildResponse(HttpStatus.NOT_FOUND,
                "ROUTE_404",
                "No endpoint found for: " + request.getRequestURI(),
                request.getRequestURI());
    }

    // 🔹 Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unhandled error: ", ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "SYS_500",
                "Something went wrong. Please try again later.",
                request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String errorCode,
            String message,
            String path) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                errorCode,
                message,
                path
        );

        return new ResponseEntity<>(error, status);
    }
}
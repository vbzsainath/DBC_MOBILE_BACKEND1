package com.vbz.dbcards.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;

    public ErrorResponse(LocalDateTime timestamp, int status, String error,
                         String errorCode, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}
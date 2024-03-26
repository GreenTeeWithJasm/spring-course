package com.sasha.course.entities.Student;

import org.springframework.http.HttpStatus;

public class StudentErrorResponse {
    private int status;
    private String message;
    private long timestamp;

    public int getStatus() {
        return status;
    }

    public StudentErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public void setStatus(HttpStatus status) {
        this.status = status.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

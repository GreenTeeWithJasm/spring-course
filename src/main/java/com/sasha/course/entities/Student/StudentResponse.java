package com.sasha.course.entities.Student;

import org.springframework.http.HttpStatus;

public class StudentResponse {
    private String message;
    private int status;
    private long timestamp;

    public StudentResponse(StudentResponseMessage message, HttpStatus status) {
        this.message = message.getValue();
        this.status = status.value();
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

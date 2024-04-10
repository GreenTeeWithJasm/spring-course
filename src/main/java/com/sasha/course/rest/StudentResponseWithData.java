package com.sasha.course.rest;

import com.sasha.course.entities.Student.StudentResponse;
import com.sasha.course.entities.Student.StudentResponseMessage;
import org.springframework.http.HttpStatus;

public class StudentResponseWithData<T> extends StudentResponse {
    private T data;

    public StudentResponseWithData(StudentResponseMessage message, HttpStatus status, T data) {
        super(message, status);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}

package com.sasha.course.dao;

public class NotFoundStudentsException extends StudentException {
    public NotFoundStudentsException(String message) {
        super(message);
    }
}

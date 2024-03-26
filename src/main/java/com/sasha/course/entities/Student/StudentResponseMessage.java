package com.sasha.course.entities.Student;

public enum StudentResponseMessage {
    SUCCESS("Success");

    private String value;

    StudentResponseMessage(String val) {
        this.value = val;
    }

    public String getValue() {
        return value;
    }
}

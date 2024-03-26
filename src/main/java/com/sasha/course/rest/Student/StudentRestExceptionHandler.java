package com.sasha.course.rest.Student;

import com.sasha.course.dao.AddStudentException;
import com.sasha.course.dao.StudentException;
import com.sasha.course.entities.Student.StudentErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class StudentRestExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(StudentException exc) {
        StudentErrorResponse error = new StudentErrorResponse(HttpStatus.NOT_FOUND, exc.getMessage());

        return new ResponseEntity<StudentErrorResponse> (error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(AddStudentException exc) {
        StudentErrorResponse error = new StudentErrorResponse(HttpStatus.BAD_REQUEST, exc.getMessage());

        return new ResponseEntity<StudentErrorResponse> (error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(NoResourceFoundException exc) {
        StudentErrorResponse error = new StudentErrorResponse(HttpStatus.NOT_FOUND, exc.getMessage());

        return new ResponseEntity<StudentErrorResponse> (error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(Exception exc) {
        StudentErrorResponse error = new StudentErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

        return new ResponseEntity<StudentErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

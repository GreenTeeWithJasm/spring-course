package com.sasha.course.services;

import com.sasha.course.entities.Student.Student;

import java.util.List;

public interface StudentServiceInterface {
    int createStudent(Student student);

    Student getStudentById(int id);

    List<Student> getStudentsByLastName(String lastName);

    List<Student> getAllStudents();

    Student updateSingleStudent(Student studentToUpdate);

    int updateStudentsLastNames(String oldLastName, String newLastName);

    void deleteStudentById(int id);
}

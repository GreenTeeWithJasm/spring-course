package com.sasha.course.dao;

import com.sasha.course.entities.Student.Student;
import com.sasha.course.entities.Student.StudentResponse;
import com.sasha.course.rest.Student.StudentResponseWithData;

import java.util.List;

public interface StudentDAO {
    StudentResponse save(Student student);

    StudentResponseWithData<Student> findById(int id);

    StudentResponseWithData<List<Student>> findByLastName(String lastName);

    StudentResponseWithData<List<Student>> findAll();

    StudentResponse update(Student student);

    StudentResponse updateAllLastNames(String oldLastName, String newLastName);

    StudentResponse deleteById(int id);
}

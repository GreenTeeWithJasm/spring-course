package com.sasha.course.dao;

import com.sasha.course.entities.Student.Student;

import java.util.List;

public interface StudentDAO {
    void save(Student student);

    Student findById(int id);

    List<Student> findByLastName(String lastName);

    List<Student> findAll();

    Student update(Student student);

    int updateAllLastNames(String oldLastName, String newLastName);

    void delete(Student student);
}

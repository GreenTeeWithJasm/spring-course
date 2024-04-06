package com.sasha.course.repositories;

import com.sasha.course.entities.Student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByLastName(String lastName);


    @Modifying
    @Query("UPDATE Student SET firstName = ?2, lastName = ?3, email = ?4 WHERE id = ?1")
    void updateOne(int id, String firstName, String lastName, String email);

    @Modifying
    @Query("UPDATE Student SET lastName = ?2 WHERE lastName = ?1")
    int updateLastNames(String oldLastName, String newLastName);
}

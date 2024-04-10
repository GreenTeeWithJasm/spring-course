package com.sasha.course.repositories;

import com.sasha.course.entities.StudentUser.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentUserRepository extends JpaRepository<StudentUser, Integer> {
    Optional<StudentUser> findByEmail(String email);
}

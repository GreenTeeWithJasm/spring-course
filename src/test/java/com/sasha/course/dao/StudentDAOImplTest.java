package com.sasha.course.dao;

import com.sasha.course.entities.Student.Student;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class StudentDAOImplTest {
    private final StudentDAO studentDAO;
    private final EntityManager entityManager;

    @Autowired
    public StudentDAOImplTest(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.studentDAO = new StudentDAOImpl(entityManager);
    }

    @Test
    public void createStudent() {
        Student newStudent = new Student("Sasha", "Guralnik", "sasha@mail.com");
        studentDAO.save(newStudent);

        entityManager.flush();
        entityManager.clear();

        Student persistedStudent = entityManager.createQuery("FROM Student WHERE firstName = 'Sasha'", Student.class).getSingleResult();

        Assertions.assertEquals(persistedStudent.getLastName(), "Guralnik");
    }
}

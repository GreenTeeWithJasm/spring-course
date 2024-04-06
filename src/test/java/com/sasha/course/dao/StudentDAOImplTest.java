package com.sasha.course.dao;

import com.sasha.course.entities.Student.Student;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentDAOImplTest {
    private final EntityManager entityManager;
    private final StudentDAO studentDAO;

    @Autowired
    public StudentDAOImplTest(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.studentDAO = new StudentDAOImpl(entityManager);
    }

    public Student getTestStudent() {
        return new Student("test", "user", "test@user.com");
    }
    public Student getTestStudent(int id) {
        Student student = new Student("test", "user", "test@user.com");
        student.setId(id);

        return student;
    }

    @Test
    public void createStudent() {
        studentDAO.save(getTestStudent());

        assertEquals(
                entityManager.createQuery(
                        "FROM Student WHERE lastName = 'user' AND firstName = 'test'",
                        Student.class
                ).getResultList().get(0).getLastName(), "user");
    }

    @Test
    public void createExistingUser() {
        entityManager.persist(getTestStudent());

        ConstraintViolationException exc = assertThrows(ConstraintViolationException.class, () -> studentDAO.save(getTestStudent()));

        assertEquals(exc.getConstraintName(), "student.last_name_email_unique");
    }

    @Test
    public void getStudentById() {
        Student student = getTestStudent();
        entityManager.persist(student);

        Student studentFoundByDao = studentDAO.findById(student.getId());

        assertEquals(student.getId(), studentFoundByDao.getId());
    }

    @Test
    public void getNonExistingUser() {
        Student studentFoundByDao = studentDAO.findById(22);

        Assertions.assertNull(studentFoundByDao);
    }

    @Test
    public void getStudentsByLastName() {
        String LAST_NAME_FOR_TEST = "Guralnik";

        List<Student> studentList = entityManager.createQuery("FROM Student WHERE lastName = '%s'".formatted(LAST_NAME_FOR_TEST),
                Student.class).getResultList();

        List<Student> studentListFromDAO = studentDAO.findByLastName(LAST_NAME_FOR_TEST);

        assertEquals(studentList.size(), studentListFromDAO.size());
    }

    @Test
    public void getAllStudents() {
        List<Student> studentList = entityManager.createQuery("FROM Student",
                Student.class).getResultList();

        List<Student> studentListFromDAO = studentDAO.findAll();

        assertEquals(studentList.size(), studentListFromDAO.size());
    }

    @Test
    public void updateStudent() {
        Student student = getTestStudent();
        entityManager.persist(student);

        assertEquals("test@user.com", student.getEmail());

        student.setEmail("test@student.com");
        Student updatedStudent = studentDAO.update(student);

        assertEquals("test@student.com", updatedStudent.getEmail());
    }

    @Test
    public void updateNotExistingStudent() {
        Student student = getTestStudent();

        assertEquals("test@user.com", student.getEmail());

        student.setEmail("test@student.com");
        Student updatedStudent = studentDAO.update(student);

        assertEquals("test@student.com", updatedStudent.getEmail());
    }

    @Test
    public void updateAllLastNames() {
        entityManager.persist(new Student("test1", "user", "test1@user.com"));
        entityManager.persist(new Student("test2", "user", "test2@user.com"));
        entityManager.persist(new Student("test3", "user1", "test3@user1.com"));

        assertEquals(2, studentDAO.updateAllLastNames("user", "user3"));

        assertEquals(0, studentDAO.updateAllLastNames("user", "user4"));
    }

    @Test
    public void delete() {
        Student studentToDelete = getTestStudent();

        entityManager.persist(studentToDelete);
        int assignedId = studentToDelete.getId();
        Student insertedStudent = entityManager.find(Student.class, assignedId);

        assertEquals(studentToDelete.getEmail(), insertedStudent.getEmail());

        studentDAO.delete(studentToDelete);

        assertNull(entityManager.find(Student.class, assignedId));
    }

    @Test
    public void deleteNonExistingStudent() {
        Student studentToDelete = getTestStudent(1);

        assertThrows(IllegalArgumentException.class, () -> studentDAO.delete(studentToDelete));
    }
}

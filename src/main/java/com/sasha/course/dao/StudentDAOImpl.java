package com.sasha.course.dao;

import com.sasha.course.entities.Student.Student;
import com.sasha.course.entities.Student.StudentResponse;
import com.sasha.course.entities.Student.StudentResponseMessage;
import com.sasha.course.rest.Student.StudentResponseWithData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class StudentDAOImpl implements StudentDAO {
    private EntityManager entityManager;

    @Autowired
    public StudentDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public StudentResponse save(Student student) {
        TypedQuery<Student> findMatchesQuery = entityManager.createQuery("FROM Student WHERE lastName = :lastName AND email = :email", Student.class);
        findMatchesQuery.setParameter("lastName", student.getLastName());
        findMatchesQuery.setParameter("email", student.getEmail());

        List<Student> matches = findMatchesQuery.getResultList();

        if (!matches.isEmpty()) {
            throw new AddStudentException("The student with the provided Last Name and Email already exists");
        }
        entityManager.persist(student);
        return new StudentResponse(StudentResponseMessage.SUCCESS, HttpStatus.CREATED);
    }

    @Override
    public StudentResponseWithData<Student> findById(int id) {
        Student studentById = entityManager.find(Student.class, id);

        if (studentById == null) {
            throw new NotFoundStudentsException("The student with ID %d is not found".formatted(id));
        }

        return new StudentResponseWithData<>(StudentResponseMessage.SUCCESS, HttpStatus.OK, studentById);
    }

    @Override
    public StudentResponseWithData<List<Student>> findByLastName(String lastName) {
        TypedQuery<Student> query = entityManager.createQuery("FROM Student WHERE lastName = :lastName order by firstName", Student.class);
        query.setParameter("lastName", lastName);

        List<Student> matches = query.getResultList();

        if (matches.isEmpty()) {
            throw new NotFoundStudentsException("The students with Last Name %s are not found".formatted(lastName));
        }
        return new StudentResponseWithData<>(StudentResponseMessage.SUCCESS, HttpStatus.OK, matches);
    }

    @Override
    public StudentResponseWithData<List<Student>> findAll() {
        TypedQuery<Student> query = entityManager.createQuery("FROM Student", Student.class);
        List<Student> matches = query.getResultList();
        if (matches.isEmpty()) {
            throw new NotFoundStudentsException("No students were found");
        }
        return new StudentResponseWithData<>(StudentResponseMessage.SUCCESS, HttpStatus.OK, matches);
    }

    @Override
    @Transactional
    public StudentResponse update(Student student) {
        entityManager.merge(student);
        return new StudentResponse(StudentResponseMessage.SUCCESS, HttpStatus.OK);
    }

    @Override
    @Transactional
    public StudentResponse updateAllLastNames(String oldLastName, String newLastName) {
        Query query = entityManager.createQuery("UPDATE Student SET lastName = :newLastName WHERE lastName = :oldLastName");
        query.setParameter("newLastName", newLastName);
        query.setParameter("oldLastName", oldLastName);
        query.executeUpdate();
        return new StudentResponse(StudentResponseMessage.SUCCESS, HttpStatus.OK);
    }

    @Override
    @Transactional
    public StudentResponse deleteById(int id) {
        Student studentToRemove = entityManager.find(Student.class, id);
        entityManager.remove(studentToRemove);
        return new StudentResponse(StudentResponseMessage.SUCCESS, HttpStatus.OK);
    }
}

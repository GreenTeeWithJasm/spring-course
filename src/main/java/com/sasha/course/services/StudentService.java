package com.sasha.course.services;

import com.sasha.course.dao.AddStudentException;
import com.sasha.course.dao.NotFoundStudentsException;
import com.sasha.course.entities.Student.Student;
import com.sasha.course.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements StudentServiceInterface {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public int createStudent(Student student) {
        try {
            studentRepository.save(student);
            return student.getId();
        } catch (DataIntegrityViolationException exception) {
            throw new AddStudentException("The student with the provided Last Name and Email already exists");
        }
    }

    public Student getStudentById(int id) {
        Optional<Student> studentById = studentRepository.findById(id);

        if (studentById.isEmpty()) {
            throw new NotFoundStudentsException("The student with ID %d is not found".formatted(id));
        }

        return studentById.get();
    }

    public List<Student> getStudentsByLastName(String lastName) {
        List<Student> studentList = studentRepository.findByLastName(lastName);

        if (studentList.isEmpty()) {
            throw new NotFoundStudentsException("The students with lastName %s are not found".formatted(lastName));
        }

        return studentList;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = studentRepository.findAll();

        if (studentList.isEmpty()) {
            throw new NotFoundStudentsException("The students list is empty");
        }

        return studentList;
    }

    @Transactional
    public Student updateSingleStudent(Student studentToUpdate) {
        studentRepository.updateOne(
                studentToUpdate.getId(),
                studentToUpdate.getFirstName(),
                studentToUpdate.getLastName(),
                studentToUpdate.getEmail()
        );
        return studentToUpdate;
    }

    @Transactional
    public int updateStudentsLastNames(String oldLastName, String newLastName) {
        int entriesUpdated = studentRepository.updateLastNames(oldLastName, newLastName);

        if (entriesUpdated == 0) {
            throw new NotFoundStudentsException("Not found students to update");
        }

        return entriesUpdated;
    }

    public void deleteStudentById(int id) {
        Optional<Student> studentToDelete = studentRepository.findById(id);

        if (studentToDelete.isEmpty()) {
            throw new NotFoundStudentsException("Not found students to delete");
        }

        studentRepository.delete(studentToDelete.get());
    }
}

package com.sasha.course.services;

import com.sasha.course.dao.NotFoundStudentsException;
import com.sasha.course.entities.Student.Student;
import com.sasha.course.repositories.StudentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;

public class StudentsServiceTest {
    private final StudentService studentService;
    private final StudentRepository studentRepository;

    @Autowired
    public StudentsServiceTest() {
        this.studentRepository = Mockito.mock(StudentRepository.class);
        this.studentService = new StudentService(studentRepository);
    }

    private Student getStudentForTest() {
        return new Student("test", "user", "test@user.com");
    }

    private Student getStudentForTest(Integer id) {
        Student testStudent = getStudentForTest();
        testStudent.setId(id);
        return testStudent;
    }

    @Test
    public void createStudent() {
        Student student = getStudentForTest();
        doAnswer(invocationOnMock -> {
            student.setId(1);
            return null;
        }).when(studentRepository).save(student);



        int newStudentId = studentService.createStudent(student);

        assertEquals(newStudentId, 1);
    }

    @Test
    public void getStudentById() {
        Student student = getStudentForTest(12);
        Mockito.when(studentRepository.findById(12)).thenReturn(Optional.of(student));

        assertEquals(student, studentService.getStudentById(12));
    }

    @Test
    public void getNotExistingStudentById() {
        Mockito.when(studentRepository.findById(12)).thenReturn(Optional.empty());

        assertThrows(NotFoundStudentsException.class, () -> studentService.getStudentById(12));
    }

    @Test
    public void getStudentsByLastName() {
        String testLastName = "test last name";
        Mockito.when(studentRepository.findByLastName(testLastName)).thenReturn(List.of(
                getStudentForTest(),
                getStudentForTest()
        ));

        List<Student> foundStudents = studentService.getStudentsByLastName(testLastName);

        assertEquals(2, foundStudents.size());
    }

    @Test
    public void getStudentsByNotExistingLastName() {
        String testLastName = "test last name";
        Mockito.when(studentRepository.findByLastName(testLastName)).thenReturn(List.of());

        assertThrows(NotFoundStudentsException.class, () -> studentService.getStudentsByLastName(testLastName));
    }

    @Test
    public void getAllStudents() {
        Mockito.when(studentRepository.findAll()).thenReturn(List.of(
                getStudentForTest(),
                getStudentForTest()
        ));

        List<Student> foundStudents = studentService.getAllStudents();

        assertEquals(2, foundStudents.size());
    }

    @Test
    public void getAllStudentsWithNoStudents() {
        Mockito.when(studentRepository.findAll()).thenReturn(List.of());

        assertThrows(NotFoundStudentsException.class, studentService::getAllStudents);
    }

    @Test
    public void updateSingleStudent() {
        Student studentToUpdate = getStudentForTest();
        studentToUpdate.setEmail("test@student.com");

        Student updatedStudent = studentService.updateSingleStudent(studentToUpdate);

        assertEquals("test@student.com", updatedStudent.getEmail());
    }

    @Test
    public void updateStudentsLastNames() {
        Mockito.when(studentRepository.updateLastNames(anyString(), anyString())).thenReturn(3);

        int updatedRows = studentService.updateStudentsLastNames("user", "student");

        assertEquals(3, updatedRows);
    }

    @Test
    public void updateStudentsLastNamesWithNoMatches() {
        Mockito.when(studentRepository.updateLastNames(anyString(), anyString())).thenReturn(0);

        assertThrows(NotFoundStudentsException.class, () -> studentService.updateStudentsLastNames("user", "student"));
    }

    @Test
    public void deleteStudentById() {
        ProxyStudent studentToDelete = new ProxyStudent(getStudentForTest(1));
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.of(studentToDelete.getProxyStudent()));
        doAnswer(invocationOnMock -> {
            studentToDelete.removeStudent();
            return null;
        }).when(studentRepository).delete(any(Student.class));

        assertEquals(1, studentToDelete.getProxyStudent().getId());

        studentService.deleteStudentById(1);

        assertNull(studentToDelete.getProxyStudent());
    }

    @Test
    public void deleteNonExistingStudentById() {
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundStudentsException.class, () -> studentService.deleteStudentById(1));
    }
}

class ProxyStudent {
    private Student proxyStudent;

    public ProxyStudent(Student student) {
        this.proxyStudent = student;
    }

    public Student getProxyStudent() {
        return proxyStudent;
    }

    public void removeStudent() {
        proxyStudent = null;
    }
}

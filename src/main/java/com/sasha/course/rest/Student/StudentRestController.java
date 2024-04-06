package com.sasha.course.rest.Student;

import com.sasha.course.dao.BadRequestStudentsException;
import com.sasha.course.entities.Student.Student;
import com.sasha.course.entities.Student.StudentResponse;
import com.sasha.course.entities.Student.StudentResponseMessage;
import com.sasha.course.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentRestController {

    private final StudentService studentService;

    @Autowired
    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse addStudent(@RequestBody Student studentData) {
        studentService.createStudent(studentData);

        return new StudentResponse(StudentResponseMessage.SUCCESS, HttpStatus.CREATED);
    }

    @PutMapping("")
    public StudentResponse updateSingleStudent(
            @RequestParam(required = false) String oldLastName,
            @RequestParam(required = false) String newLastName,
            @RequestBody(required = false) Student studentData
    ) {
        if (oldLastName == null && newLastName == null && studentData != null) {
            return new StudentResponseWithData<Student>(StudentResponseMessage.SUCCESS, HttpStatus.OK, studentService.updateSingleStudent(studentData));
        }

        if (oldLastName != null && newLastName != null) {
            return new StudentResponseWithData<Integer>(StudentResponseMessage.SUCCESS, HttpStatus.OK, studentService.updateStudentsLastNames(oldLastName, newLastName));
        }

        throw new BadRequestStudentsException("Bad request to students update");
    }

    @GetMapping("")
    public StudentResponseWithData<List<Student>> getStudentsByLastName(@RequestParam(required = false) String lastName) {
        List<Student> studentsList = null;

        if (lastName == null) {
            studentsList = studentService.getAllStudents();
        }
        if (lastName != null) {
            studentsList = studentService.getStudentsByLastName(lastName);
        }

        return new StudentResponseWithData<>(StudentResponseMessage.SUCCESS, HttpStatus.OK, studentsList);
    }

    @GetMapping("/{studentId}")
    public StudentResponse getStudentById(@PathVariable int studentId) {
        Student student = studentService.getStudentById(studentId);
        return new StudentResponseWithData<Student>(StudentResponseMessage.SUCCESS, HttpStatus.OK, student);
    }

    @DeleteMapping("/{studentId}")
    public StudentResponse deleteStudentById(@PathVariable int studentId) {
        studentService.deleteStudentById(studentId);

        return new StudentResponse(StudentResponseMessage.SUCCESS, HttpStatus.OK);
    }
}

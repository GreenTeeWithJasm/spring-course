package com.sasha.course.rest.Student;

import com.sasha.course.dao.StudentDAO;
import com.sasha.course.entities.Student.Student;
import com.sasha.course.entities.Student.StudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentRestController {

    private StudentDAO studentDAO;

    @Autowired
    public StudentRestController(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    @GetMapping("")
    public StudentResponse getAllStudents(@RequestParam String lastName) {
        if (lastName != null) {
            return studentDAO.findByLastName(lastName);
        }
        return studentDAO.findAll();
    }

    @PostMapping("")
    public StudentResponse addStudent(@RequestBody Student studentData) {
        return studentDAO.save(studentData);
    }

    @PutMapping("")
    public StudentResponse updateStudent(@RequestBody Student studentData) {
        return studentDAO.update(studentData);
    }

    @GetMapping("/{studentId}")
    public StudentResponse getStudentById(@PathVariable int studentId) {
        return studentDAO.findById(studentId);
    }
}

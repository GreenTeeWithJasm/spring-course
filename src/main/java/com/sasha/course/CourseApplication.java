package com.sasha.course;

import com.sasha.course.repositories.StudentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CourseApplication {
	@Autowired
	private StudentUserRepository studentUserRepository;

	public static void main(String[] args) {
		SpringApplication.run(CourseApplication.class, args);
	}
}

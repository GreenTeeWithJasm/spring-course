package com.sasha.course.rest.login;

import com.sasha.course.dao.NotFoundStudentsException;
import com.sasha.course.entities.StudentUser.StudentUser;
import com.sasha.course.repositories.StudentUserRepository;
import com.sasha.course.security.jwt.AccessTokens;
import com.sasha.course.security.jwt.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginRestController {
    private final StudentUserRepository studentUserRepository;
    private final JwtHelper jwtHelper;

    @Autowired
    public LoginRestController(StudentUserRepository studentUserRepository, JwtHelper jwtHelper) {
        this.studentUserRepository = studentUserRepository;
        this.jwtHelper = jwtHelper;
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequestBody loginRequestBody) {
        Optional<StudentUser> user = studentUserRepository.findByEmail(loginRequestBody.getEmail());

        if (user.isEmpty()) {
            throw new NotFoundStudentsException("The user is not found");
        }

        AccessTokens tokens = jwtHelper.generateToken(loginRequestBody.getEmail());

        return new LoginResponse(tokens.token(), tokens.refresh());
    }
}

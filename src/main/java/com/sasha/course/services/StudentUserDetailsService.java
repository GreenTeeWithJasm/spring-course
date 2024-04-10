package com.sasha.course.services;

import com.sasha.course.components.StudentUserDetails;
import com.sasha.course.repositories.StudentUserRepository;
import com.sasha.course.entities.StudentUser.StudentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentUserDetailsService implements UserDetailsService {
    private final StudentUserRepository studentUserRepository;

    @Autowired
    public StudentUserDetailsService(StudentUserRepository studentUserRepository) {
        this.studentUserRepository = studentUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<StudentUser> studentUser = studentUserRepository.findByEmail(username);
        if (studentUser.isEmpty()) {
            throw new UsernameNotFoundException("No user with such email");
        }

        StudentUser user = studentUser.get();

        List<GrantedAuthority> authorities = user.getRoles()
                .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        return new StudentUserDetails(user.getEmail(), user.getPassword(), authorities);
    }
}

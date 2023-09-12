package com.bundleebackend.bundleebackend.util;

import com.bundleebackend.bundleebackend.entity.User;
import com.bundleebackend.bundleebackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Optional;

@Component
public class AuthUtil {
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthUtil(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public Optional<User> getUserFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            return Optional.empty();
        }

        String token = header.split(" ")[1].trim();

        if (!jwtUtil.validateToken(token)) {
            return Optional.empty();
        }

        String[] tokenSubject = jwtUtil.getSubject(token).split(",");
        String username = tokenSubject[1];
        return userRepository.findByUsername(username);
    }
}

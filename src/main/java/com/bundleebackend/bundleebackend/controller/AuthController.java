package com.bundleebackend.bundleebackend.controller;

import com.bundleebackend.bundleebackend.entity.User;
import com.bundleebackend.bundleebackend.repository.UserRepository;
import com.bundleebackend.bundleebackend.types.AuthRequest;
import com.bundleebackend.bundleebackend.types.AuthResponse;
import com.bundleebackend.bundleebackend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    UserRepository userRepository;
    JwtUtil jwtUtil;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest req, @RequestBody AuthRequest request) {
        Optional<User> getUser = userRepository.findByUsername(request.getUsername());

        if (!getUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User userGotten = getUser.get();

        if (!BCrypt.checkpw(request.getPassword(), userGotten.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(userGotten);
        AuthResponse response = new AuthResponse(userGotten.getUsername(), token);
        return ResponseEntity.ok().body(response);
    }
}

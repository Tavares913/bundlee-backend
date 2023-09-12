package com.bundleebackend.bundleebackend.controller;

import com.bundleebackend.bundleebackend.entity.User;
import com.bundleebackend.bundleebackend.repository.UserRepository;
import com.bundleebackend.bundleebackend.types.AuthRequest;
import com.bundleebackend.bundleebackend.types.AuthResponse;
import com.bundleebackend.bundleebackend.types.MessageResponse;
import com.bundleebackend.bundleebackend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
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

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {
    UserRepository userRepository;
    JwtUtil jwtUtil;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletRequest req, @RequestBody AuthRequest authInfo) {
        Optional<User> existingUser = userRepository.findByUsername(authInfo.getUsername());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>("Username already taken.", HttpStatus.BAD_REQUEST);
        }
        User newUser = new User(authInfo.getUsername(), BCrypt.hashpw(authInfo.getPassword(), BCrypt.gensalt(4)));
        userRepository.save(newUser);
        return ResponseEntity.ok().body(new MessageResponse("user created."));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest req, @RequestBody AuthRequest authInfo) {
        Optional<User> getUser = userRepository.findByUsername(authInfo.getUsername());

        if (getUser.isEmpty()) {
           return new ResponseEntity<>("Username or password is incorrect.", HttpStatus.BAD_REQUEST);
        }

        User userGotten = getUser.get();

        if (!BCrypt.checkpw(authInfo.getPassword(), userGotten.getPassword())) {
            return new ResponseEntity<>("Username or password is incorrect.", HttpStatus.BAD_REQUEST);
        }

        String token = jwtUtil.generateToken(userGotten);
        AuthResponse response = new AuthResponse(userGotten.getUsername(), token);
        return ResponseEntity.ok().body(response);
    }
}

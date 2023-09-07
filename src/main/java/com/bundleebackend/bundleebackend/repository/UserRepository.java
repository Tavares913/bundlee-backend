package com.bundleebackend.bundleebackend.repository;

import com.bundleebackend.bundleebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "users")
public  interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}

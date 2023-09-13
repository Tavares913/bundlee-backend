package com.bundleebackend.bundleebackend.repository;

import com.bundleebackend.bundleebackend.entity.Individual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "collections")
public interface IndividualRepository extends JpaRepository<Individual, Integer> {
    Optional<Individual> findByPlatformId(String platformId);
}

package com.bundleebackend.bundleebackend.repository;

import com.bundleebackend.bundleebackend.entity.Collection;
import com.bundleebackend.bundleebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "collections")
public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    Optional<List<Collection>> findByUser(User user);
}

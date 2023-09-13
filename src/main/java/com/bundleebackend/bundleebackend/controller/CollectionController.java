package com.bundleebackend.bundleebackend.controller;

import com.bundleebackend.bundleebackend.entity.Collection;
import com.bundleebackend.bundleebackend.entity.Individual;
import com.bundleebackend.bundleebackend.entity.User;
import com.bundleebackend.bundleebackend.repository.CollectionRepository;
import com.bundleebackend.bundleebackend.repository.IndividualRepository;
import com.bundleebackend.bundleebackend.repository.UserRepository;
import com.bundleebackend.bundleebackend.types.MessageResponse;
import com.bundleebackend.bundleebackend.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/collection")
public class CollectionController {

    private CollectionRepository collectionRepository;
    private UserRepository userRepository;
    private IndividualRepository individualRepository;
    private AuthUtil authUtil;

    @Autowired
    public CollectionController(CollectionRepository collectionRepository, UserRepository userRepository, AuthUtil authUtil, IndividualRepository individualRepository) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
        this.individualRepository = individualRepository;
    }

    @PostMapping("/create-edit")
    public ResponseEntity<?> addToCollection(HttpServletRequest request, @RequestBody Collection collection) {

        Optional<User> user = authUtil.getUserFromRequest(request);
        if (user.isEmpty()) return new ResponseEntity<>("No user authenticated.", HttpStatus.BAD_REQUEST);
        if (collection.getId() == -1) {
            System.out.println(1);
            System.out.println(1.1);
            Collection newCollection = new Collection();
            newCollection.setName(collection.getName());
            newCollection.setUser(user.get());
            System.out.println(1.2);
            ArrayList<Individual> savedIndividuals = new ArrayList<>();
            for (int i = 0; i < collection.getIndividuals().size(); ++i) {
                Individual savedIndividual = createOrUpdateIndividual(collection.getIndividuals().get(i));
                savedIndividuals.add(savedIndividual);
            }
            System.out.println(1.3);
            newCollection.setIndividuals(savedIndividuals);
            System.out.println(1.4);
            collectionRepository.save(newCollection);
            System.out.println(1.5);
            return ResponseEntity.ok().body(new MessageResponse("Successfully created collection."));
        }

        System.out.println(2);
        System.out.println(2.1);
        Optional<Collection> prevCollectionOpt = collectionRepository.findById(collection.getId());
        System.out.println(2.2);
        if (prevCollectionOpt.isEmpty()) return new ResponseEntity<>("Unable to find collection to update.", HttpStatus.BAD_REQUEST);
        Collection prevCollection = prevCollectionOpt.get();
        prevCollection.setName(collection.getName());
        System.out.println(2.3);
        ArrayList<Individual> savedIndividuals = new ArrayList<>();
        for (int i = 0; i < collection.getIndividuals().size(); ++i) {
            Individual savedIndividual = createOrUpdateIndividual(collection.getIndividuals().get(i));
            savedIndividuals.add(savedIndividual);
        }
        System.out.println(2.4);
        prevCollection.setIndividuals(savedIndividuals);
        System.out.println(2.5);
        collectionRepository.save(prevCollection);
        System.out.println(2.6);
        return ResponseEntity.ok().body(new MessageResponse("Successfully edited collection."));
    }

    public Individual createOrUpdateIndividual(Individual individual) {
        Optional<Individual> prevIndividualOpt = individualRepository.findByPlatformId(individual.getPlatformId());
        if (prevIndividualOpt.isEmpty()) {
            Individual newIndividual = new Individual();
            newIndividual.setPlatformId(individual.getPlatformId());
            newIndividual.setPlatform(individual.getPlatform());
            newIndividual.setTitle(individual.getTitle());
            newIndividual.setYear(individual.getYear());
            newIndividual.setDescription(individual.getDescription());
            newIndividual.setStatus(individual.getStatus());
            Individual result = individualRepository.save(newIndividual);
            System.out.println(result);
            return result;
        }
        Individual prevIndividual = prevIndividualOpt.get();
        prevIndividual.setPlatformId(individual.getPlatformId());
        prevIndividual.setPlatform(individual.getPlatform());
        prevIndividual.setTitle(individual.getTitle());
        prevIndividual.setYear(individual.getYear());
        prevIndividual.setDescription(individual.getDescription());
        prevIndividual.setStatus(individual.getStatus());
        Individual result = individualRepository.save(prevIndividual);
        System.out.println(result);
        return result;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getCollections(HttpServletRequest request) {
        Optional<User> user = authUtil.getUserFromRequest(request);
        if (user.isEmpty()) return new ResponseEntity<>("No user authenticated.", HttpStatus.BAD_REQUEST);
        Optional<List<Collection>> userCollections = collectionRepository.findByUser(user.get());
        if (userCollections.isEmpty()) return ResponseEntity.ok().body(new ArrayList<>());
        List<Collection> userCollectionsConcrete = userCollections.get();
        ArrayList<Collection> retval = new ArrayList<Collection>();
        for (int i = 0; i < userCollectionsConcrete.size(); ++i) {
            Collection curCol = userCollectionsConcrete.get(i);
            Collection curRetCol = new Collection();
            curRetCol.setId(curCol.getId());
            curRetCol.setName(curCol.getName());
            curRetCol.setIndividuals(curCol.getIndividuals());
            retval.add(curRetCol);
        }
        return ResponseEntity.ok().body(retval);
    }
}

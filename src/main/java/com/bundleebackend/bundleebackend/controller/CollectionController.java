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

import javax.swing.text.html.Option;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            Collection newCollection = new Collection();
            newCollection.setName(collection.getName());
            newCollection.setUser(user.get());
            ArrayList<Individual> savedIndividuals = new ArrayList<>();
            for (int i = 0; i < collection.getIndividuals().size(); ++i) {
                Individual savedIndividual = createOrUpdateIndividual(collection.getIndividuals().get(i));
                savedIndividuals.add(savedIndividual);
            }
            newCollection.setIndividuals(savedIndividuals);
            collectionRepository.save(newCollection);
            return ResponseEntity.ok().body(new MessageResponse("Successfully created collection."));
        }
        Optional<Collection> prevCollectionOpt = collectionRepository.findById(collection.getId());
        if (prevCollectionOpt.isEmpty()) return new ResponseEntity<>("Unable to find collection to update.", HttpStatus.BAD_REQUEST);
        Collection prevCollection = prevCollectionOpt.get();
        prevCollection.setName(collection.getName());
        ArrayList<Individual> savedIndividuals = new ArrayList<>();
        for (int i = 0; i < collection.getIndividuals().size(); ++i) {
            Individual savedIndividual = createOrUpdateIndividual(collection.getIndividuals().get(i));
            savedIndividuals.add(savedIndividual);
        }
        prevCollection.setIndividuals(savedIndividuals);
        collectionRepository.save(prevCollection);
        return ResponseEntity.ok().body(new MessageResponse("Successfully edited collection."));
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

    @GetMapping("/search")
    public ResponseEntity<?> searchCollections(HttpServletRequest request, @RequestParam String name) {
        List<Collection> retval = this.collectionRepository.findByNameContaining(name);
        for (int i = 0; i < retval.size(); ++i) {
            Collection curCol = retval.get(i);
            User curColUser = curCol.getUser();
            curColUser.setCollections(null);
            curColUser.setPassword(null);
            curCol.setUser(curColUser);
            retval.set(i, curCol);
        }
        return ResponseEntity.ok().body(retval);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCollection(HttpServletRequest request, @RequestParam int id) {
        Optional<Collection> colToDeleteOpt = collectionRepository.findById(id);
        if (colToDeleteOpt.isEmpty()) return new ResponseEntity<>("Unable to find collection to delete.", HttpStatus.BAD_REQUEST);
        Optional<User> user = authUtil.getUserFromRequest(request);
        if (user.isEmpty()) return new ResponseEntity<>("No user authenticated.", HttpStatus.BAD_REQUEST);
        Collection coltoDelete = colToDeleteOpt.get();
        if (coltoDelete.getUser().getId() != user.get().getId()) return new ResponseEntity<>("Collection you're trying to delete does not belong to this user.", HttpStatus.BAD_REQUEST);
        collectionRepository.delete(coltoDelete);
        return ResponseEntity.ok().body(new MessageResponse("Successfully deleted collection."));
    }

    public Individual createOrUpdateIndividual(Individual individual) {
        List<Individual> prevIndividuals = individualRepository.findByPlatformId(individual.getPlatformId());
        Individual updatedIndividual = null;
        for (Individual prevIndividual : prevIndividuals) {
            if (Objects.equals(prevIndividual.getTitle(), individual.getTitle())) {
                updatedIndividual = prevIndividual;
                break;
            }
        }
        if (updatedIndividual == null) updatedIndividual = new Individual();
        updatedIndividual.setPlatform(individual.getPlatform());
        updatedIndividual.setPlatformId(individual.getPlatformId());
        updatedIndividual.setType(individual.getType());
        updatedIndividual.setTitle(individual.getTitle());
        updatedIndividual.setYear(individual.getYear());
        updatedIndividual.setDescription(individual.getDescription());
        updatedIndividual.setStatus(individual.getStatus());
        updatedIndividual.setCoverLink(individual.getCoverLink());
        updatedIndividual.setThumbnailLink(individual.getThumbnailLink());
        return individualRepository.save(updatedIndividual);
    }
}

package com.bundleebackend.bundleebackend.controller;

import com.bundleebackend.bundleebackend.entity.Collection;
import com.bundleebackend.bundleebackend.entity.User;
import com.bundleebackend.bundleebackend.repository.CollectionRepository;
import com.bundleebackend.bundleebackend.repository.UserRepository;
import com.bundleebackend.bundleebackend.types.CreateCollectionRequest;
import com.bundleebackend.bundleebackend.types.MessageResponse;
import com.bundleebackend.bundleebackend.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/collection")
public class CollectionController {

    private CollectionRepository collectionRepository;
    private UserRepository userRepository;
    private AuthUtil authUtil;

    @Autowired
    public CollectionController(CollectionRepository collectionRepository, UserRepository userRepository, AuthUtil authUtil) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> addToCollection(HttpServletRequest request, @RequestBody CreateCollectionRequest  createCollectionRequest) {
        Optional<User> user = authUtil.getUserFromRequest(request);
        if (user.isEmpty()) return new ResponseEntity<>("No user authenticated.", HttpStatus.BAD_REQUEST);
        Collection newCollection = new Collection();
        newCollection.setName(createCollectionRequest.getName());
        newCollection.setUser(user.get());
        collectionRepository.save(newCollection);
        return ResponseEntity.ok().body(new MessageResponse("Successfully created collection."));
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

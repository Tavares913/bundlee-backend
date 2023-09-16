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
import org.apache.coyote.Response;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.json.HTTP;
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
            curColUser.setFavouritedCollections(null);
            curCol.setUser(curColUser);
            List<User> curColUsersThatFavourited = curCol.getUsersThatFavourited();
            for (int j = 0; j < curColUsersThatFavourited.size(); ++j) {
                curColUsersThatFavourited.get(j).setCollections(null);
                curColUsersThatFavourited.get(j).setPassword(null);
                curColUsersThatFavourited.get(j).setFavouritedCollections(null);
            }
            curCol.setUsersThatFavourited(curColUsersThatFavourited);
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

    @PostMapping("/favourite")
    public ResponseEntity<?> favouriteCollection(HttpServletRequest request, @RequestParam int collectionId) {
        Optional<User> userOptional = authUtil.getUserFromRequest(request);
        if (userOptional.isEmpty()) return new ResponseEntity<>("No user authenticated.", HttpStatus.BAD_REQUEST);
        User user = userOptional.get();
        Optional<Collection> colToFavouriteOptional = collectionRepository.findById(collectionId);
        if (colToFavouriteOptional.isEmpty()) return new ResponseEntity<>("No collection with id " + collectionId, HttpStatus.BAD_REQUEST);
        Collection colToFavourite = colToFavouriteOptional.get();
        List<Collection> newFavouritedCollections = user.getFavouritedCollections();
        newFavouritedCollections.add(colToFavourite);
        user.setFavouritedCollections(newFavouritedCollections);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Successfully favourited collection"));
    }

    @PostMapping("/unfavourite")
    public ResponseEntity<?> unfavouriteCollection(HttpServletRequest request, @RequestParam int collectionId) {
        Optional<User> userOptional = authUtil.getUserFromRequest(request);
        if (userOptional.isEmpty()) return new ResponseEntity<>("No user authenticated.", HttpStatus.BAD_REQUEST);
        User user = userOptional.get();
        Optional<Collection> colToFavouriteOptional = collectionRepository.findById(collectionId);
        if (colToFavouriteOptional.isEmpty()) return new ResponseEntity<>("No collection with id " + collectionId, HttpStatus.BAD_REQUEST);
        Collection colToUnfavourite = colToFavouriteOptional.get();
        List<Collection> newFavouritedCollections = user.getFavouritedCollections();
        int indexToRemove = newFavouritedCollections.indexOf(colToUnfavourite);
        if (indexToRemove == -1) return new ResponseEntity<>("No such collection inside this user's favourites.", HttpStatus.BAD_REQUEST);
        newFavouritedCollections.remove(colToUnfavourite);
        user.setFavouritedCollections(newFavouritedCollections);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Successfully unfavourited collection"));
    }

    @GetMapping("/favourite")
    public ResponseEntity<?> getFavouritedCollections(HttpServletRequest request) {
        Optional<User> userOptional = authUtil.getUserFromRequest(request);
        if (userOptional.isEmpty()) return new ResponseEntity<>("No user authenticated.", HttpStatus.BAD_REQUEST);
        User user = userOptional.get();
        List<Collection> retval = user.getFavouritedCollections();
        for (int i = 0; i < retval.size(); ++i) {
            Collection curCol = retval.get(i);
            User curColUser = curCol.getUser();
            curColUser.setCollections(null);
            curColUser.setPassword(null);
            curColUser.setFavouritedCollections(null);
            curCol.setUser(curColUser);
            List<User> curColUsersThatFavourited = curCol.getUsersThatFavourited();
            for (int j = 0; j < curColUsersThatFavourited.size(); ++j) {
                curColUsersThatFavourited.get(j).setCollections(null);
                curColUsersThatFavourited.get(j).setPassword(null);
                curColUsersThatFavourited.get(j).setFavouritedCollections(null);
            }
            curCol.setUsersThatFavourited(curColUsersThatFavourited);
            retval.set(i, curCol);
        }
        return ResponseEntity.ok().body(retval);
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
        updatedIndividual.setRating(individual.getRating());
        updatedIndividual.setStatus(individual.getStatus());
        updatedIndividual.setCoverLink(individual.getCoverLink());
        updatedIndividual.setThumbnailLink(individual.getThumbnailLink());
        return individualRepository.save(updatedIndividual);
    }
}

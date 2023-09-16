package com.bundleebackend.bundleebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "collections")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(name = "collection_individual",
        joinColumns = @JoinColumn(name = "collection_id"),
        inverseJoinColumns = @JoinColumn(name = "individual_id"))
    private List<Individual> individuals;
    @ManyToMany(mappedBy = "favouritedCollections")
    private List<User> usersThatFavourited;

    public Collection() {}

    public Collection(String name, User user, List<Individual> individuals) {
        this.name = name;
        this.user = user;
        this.individuals = individuals;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user=" + user +
                ", individuals=" + individuals +
                '}';
    }
}

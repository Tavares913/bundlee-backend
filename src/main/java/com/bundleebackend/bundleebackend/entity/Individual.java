package com.bundleebackend.bundleebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "individuals")
public class Individual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "platform_id")
    private String platformId;
    @Column(name = "platform")
    private String platform;
    @Column(name = "title")
    private String title;
    @Column(name = "type")
    private String type;
    @Column(name = "year")
    private Integer year;
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private String status;
    @Column(name = "rating")
    private String rating;
    @Column(name = "cover_link")
    private String coverLink;
    @Column(name = "thumbnail_link")
    private String thumbnailLink;
    @Column(name = "extra")
    private String extra;

    public Individual() {
    }
    public Individual(String platformId, String platform, String title, String type, Integer year, String description, String status, String rating, String coverLink, String thumbnailLink, String extra) {
        this.platformId = platformId;
        this.platform = platform;
        this.title = title;
        this.type = type;
        this.year = year;
        this.description = description;
        this.status = status;
        this.rating = rating;
        this.coverLink = coverLink;
        this.thumbnailLink = thumbnailLink;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "id=" + id +
                ", platformId='" + platformId + '\'' +
                ", platform='" + platform + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", year=" + year +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", rating='" + rating + '\'' +
                ", coverLink='" + coverLink + '\'' +
                ", thumbnailLink='" + thumbnailLink + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}

package com.bundleebackend.bundleebackend.external_api_types.metacritic;

import java.util.Arrays;

public class MetacriticGame {
    private String url;
    private String title;
    private String platform;
    private int criticScore;
    private String releaseDate;
    private String developer;
    private String[] publisher;
    private String[] genres;
    private int numOfCriticReviews;
    private int numofPositiveCriticReviews;
    private int numOfMixedCriticReviews;
    private int numOfNegativeCriticReviews;
    private String productImage;


    @Override
    public String toString() {
        return "MetaCriticGetGamesResponse{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", platform='" + platform + '\'' +
                ", criticScore=" + criticScore +
                ", releaseDate='" + releaseDate + '\'' +
                ", developer='" + developer + '\'' +
                ", publisher=" + Arrays.toString(publisher) +
                ", genres=" + Arrays.toString(genres) +
                ", numOfCriticReviews=" + numOfCriticReviews +
                ", numofPositiveCriticReviews=" + numofPositiveCriticReviews +
                ", numOfMixedCriticReviews=" + numOfMixedCriticReviews +
                ", numOfNegativeCriticReviews=" + numOfNegativeCriticReviews +
                ", productImage='" + productImage + '\'' +
                '}';
    }
}

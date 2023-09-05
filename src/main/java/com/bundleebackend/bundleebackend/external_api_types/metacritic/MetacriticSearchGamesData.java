package com.bundleebackend.bundleebackend.external_api_types.metacritic;

import java.util.Arrays;

public class MetacriticSearchGamesData {
    MetacriticGame[] games;

    @Override
    public String toString() {
        return "MetacriticSearchGamesData{" +
                "games=" + Arrays.toString(games) +
                '}';
    }
}

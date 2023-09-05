package com.bundleebackend.bundleebackend.service;

import com.bundleebackend.bundleebackend.external_api_types.metacritic.MetacriticGame;
import com.bundleebackend.bundleebackend.external_api_types.metacritic.MetacriticSearchGamesResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MetacriticService {

    private String baseUrl = "https://mcgqlapi.com/api";
    private WebClient.Builder builder = WebClient.builder();

    public String getGames(String search, String platform) {
        String query  = "query getGames($title: String!, $platform: GamePlatform!) {\n" +
                "        games(input: {title: $title, platform: $platform}) {\n" +
                "          title\n" +
                "          platform\n" +
                "          criticScore\n" +
                "          url\n" +
                "          releaseDate\n" +
                "          developer\n" +
                "          publisher\n" +
                "          genres\n" +
                "          numOfCriticReviews\n" +
                "          numOfPositiveCriticReviews\n" +
                "          numOfMixedCriticReviews\n" +
                "          numOfNegativeCriticReviews\n" +
                "          productImage\n" +
                "        }\n" +
                "      }";

        JSONObject body = new JSONObject();
        body.put("query", query);
        JSONObject variables = new JSONObject();
        variables.put("title", search);
        variables.put("platform", platform);
        body.put("variables", variables.toString());

        String response = builder.build()
                .post()
                .uri(this.baseUrl)
                .bodyValue(body.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;
    }
}

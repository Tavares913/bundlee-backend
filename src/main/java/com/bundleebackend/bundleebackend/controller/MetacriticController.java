package com.bundleebackend.bundleebackend.controller;

import com.bundleebackend.bundleebackend.service.MetacriticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/metacritic")
public class MetacriticController {
    private MetacriticService metacriticService;

    @Autowired
    public MetacriticController(MetacriticService metacriticService) {
        this.metacriticService = metacriticService;
    }

    @GetMapping("/games")
    public String getGames(@RequestParam String search, @RequestParam String platform) {
        return this.metacriticService.getGames(search, platform.toUpperCase());
    }
}

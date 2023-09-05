package com.bundleebackend.bundleebackend;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class BundleeBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BundleeBackendApplication.class, args);
	}
}

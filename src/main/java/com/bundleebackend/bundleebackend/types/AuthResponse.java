package com.bundleebackend.bundleebackend.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String username;
    private String token;

    public AuthResponse() {}
    public AuthResponse(String username, String token)  {
        this.username = username;
        this.token = token;
    }
}

package com.bundleebackend.bundleebackend.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCollectionRequest {
    private String name;


    public CreateCollectionRequest() {
    }
    public CreateCollectionRequest(String name) {
        this.name = name;
    }

}

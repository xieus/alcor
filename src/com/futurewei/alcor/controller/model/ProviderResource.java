package com.futurewei.alcor.controller.model;

import lombok.Data;

@Data
public class ProviderResource {
    private String id;

    public ProviderResource(){

    }

    public ProviderResource(String id){
        this.id = id;
    }
}

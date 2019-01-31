package com.micro.common.bean;


import java.io.Serializable;

public class BaseModel implements Serializable {


    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

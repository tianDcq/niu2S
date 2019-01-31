package com.micro.frame;

import lombok.Getter;
import lombok.Setter;

public class Robot extends Role {
    protected @Getter @Setter Long hallId;

    public Robot(String uniqueId) {
        super(uniqueId);
    }
}